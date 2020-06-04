import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Converter extends Thread {
    private static TreeSet<String> links = new TreeSet<>();
    private static CountDownLatch latch = new CountDownLatch(0);
    private final Object lock = new Object();
    private String res;
    private static String path;
    private Boolean childless;
    private static AtomicBoolean onWait = new AtomicBoolean(false);
    private static ExecutorService service = Executors.newFixedThreadPool(7);
    private static long startTime;
    private Form form;

    Converter(String source, boolean Childless, Form form) {
        synchronized (lock) {
            latch = new CountDownLatch((int) (latch.getCount() + 1));
        }
        res = source;
        this.childless = Childless;
        this.form = form;
    }

    @Override
    public void run() {
        Connection con = Jsoup.connect(res).ignoreContentType(true);
        Document doc = null;
        try {
            doc = con.get();
        } catch (ConnectException | HttpStatusException | UnknownHostException | SocketTimeoutException e) {
            latch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        Elements elements = doc.select("a[href]");
        for (Element element : elements) {
            while(onWait.get()){
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String src = element.attr("abs:href");
            int countOfSlashes = (int) src.chars().filter(ch -> ch == '/').count();
            if (countOfSlashes>4)
                continue;
            if (links.size()>50000){
                if (!service.isShutdown())
                    System.out.println("The program have ran out of space");
                service.shutdownNow();
            }
            links.add(src);
            if (!childless)
                service.submit(new Converter(src,true, form));
        }
        synchronized (lock) {
            latch.countDown();
        }
        if (latch.getCount()==0||links.size()>50000){
            try {
                output(links);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void output(TreeSet<String> links) throws IOException {
        System.out.println("Output begins...");
        File outputFile = new File(path + "/WebTree.txt");
        outputFile.createNewFile();
        PrintWriter out = new PrintWriter(outputFile);
        for (String link : links){
            int counter = link.replaceAll("[^/]", "").length()-2;
            for (int i = counter; i>0; i--)
                out.write("\t");
            out.write(link + System.lineSeparator());
        }
        out.flush();
        out.close();
        System.out.println("All the links now are in the file at: " + path);
        long endTime = System.currentTimeMillis()/1000;
        long timeTaken = endTime - startTime;
        form.setInfo("The program have done its work\nAll the links now are in the file called WebTree.txt at: " + path + "\nThe work took " + timeTaken + " seconds\nTotal amount of links written is " + links.size());
    }

    void setOnWait(boolean b) {
        onWait.set(b);
    }

    boolean getOnWait() {
        return onWait.get();
    }

    ExecutorService getService() {
        return service;
    }

    void setStartTime(long startTime) {
        Converter.startTime = startTime;
    }

    void setPath(String path) {
        Converter.path = path;
    }
}
