package org.vinsert.bot;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class IOHelper {

    public static final String HTTP_USERAGENT_FAKE, HTTP_USERAGENT_REAL;

    private static String getCookie(URL url) {
           final Pattern COOKIE_PATTERN = Pattern.compile("_ddn_intercept_2_=([^;]+)");
           try {
               URLConnection conn = url.openConnection();

               BufferedReader in = new BufferedReader(
                       new InputStreamReader(
                               conn.getInputStream()));
               String inputLine;
               String tmp = "";
               while ((inputLine = in.readLine()) != null)
                   tmp += inputLine;
               in.close();
               Matcher archiveMatcher = COOKIE_PATTERN.matcher(tmp);
               if (archiveMatcher.find()) {
                   return archiveMatcher.group(0);
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
           return null;
       }

    static {
        final boolean x64 = System.getProperty("sun.arch.data.model").equals("64");
        final StringBuilder s = new StringBuilder(70);
        s.append("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; ");
        if (x64) {
            s.append("WOW64; ");
        }
        s.append("Trident/5.0)");
        HTTP_USERAGENT_FAKE = s.toString();
        s.setLength(0);
        s.append("vinsert");
        s.append('/');
        s.append("IOHelper");
        s.append(" (");
        s.append(System.getProperty("os.name"));
        s.append("; Java/");
        s.append(System.getProperty("java.version"));
        s.append(')');
        HTTP_USERAGENT_REAL = s.toString();
    }

    public static String getHttpUserAgent(final URL url) {
        return url.getHost().toLowerCase().contains("runescape") ? HTTP_USERAGENT_FAKE : HTTP_USERAGENT_REAL;
    }

    public static HttpURLConnection getHttpConnection(final URL url) throws Exception {
        final HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        con.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        con.addRequestProperty("Accept-Encoding", "gzip, deflate");
        con.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
        con.addRequestProperty("Host", url.getHost());
        con.addRequestProperty("User-Agent", getHttpUserAgent(url));
        con.addRequestProperty("Cookie:", getCookie(url));
        con.setConnectTimeout(10000);
        return con;
    }

    public static URLConnection getURLConnection(final URL url) throws Exception {
        final URLConnection con = url.openConnection();
        con.addRequestProperty("Protocol", "HTTP/1.1");
        con.addRequestProperty("Connection", "keep-alive");
        con.addRequestProperty("Keep-Alive", "200");
        con.addRequestProperty("User-Agent", getHttpUserAgent(url));
        con.addRequestProperty("Cookie:", getCookie(url));
        con.setConnectTimeout(10000);
        return con;
    }

    private static HttpURLConnection getConnection(final URL url) throws Exception {
        final HttpURLConnection con = getHttpConnection(url);
        con.setUseCaches(true);
        return con;
    }

    public static long getLastModified(final URL url) throws Exception {
        final HttpURLConnection con = getConnection(url);
        long modified = con.getLastModified();
        if (modified != 0L) {
            modified -= TimeZone.getDefault().getOffset(modified);
        }
        return modified;
    }

    public static String downloadAsString(final URL url) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Cookie:", getCookie(url));
        DataInputStream in = new DataInputStream(conn.getInputStream());
        byte b;
        try {
            while ((b = in.readByte()) != -1) {
                out.write(b);
            }
        } catch (EOFException eof) {
            //
        }
        return new String(out.toByteArray(), "UTF-8");
//		return downloadAsString(getConnection(url));
    }

    public static String downloadAsString(final HttpURLConnection con) throws Exception {
        final byte[] buffer = downloadBinary(con);
        return new String(buffer, "UTF-8");
    }

    public static byte[] downloadBinary(final URL url) throws Exception {
        return downloadBinary(getConnection(url));
    }

    public static byte[] downloadBinary(final InputStream stream) throws Exception {
        final DataInputStream in = new DataInputStream(stream);
        final byte[] buffer;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(in, out);
        buffer = out.toByteArray();
        in.close();
        return buffer;
    }

    private static byte[] downloadBinary(final URLConnection con) throws Exception {
        final DataInputStream in = new DataInputStream(getInputStream(con));
        final byte[] buffer;
        final int len = con.getContentLength();
        if (len == -1) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            write(in, out);
            buffer = out.toByteArray();
        } else {
            buffer = new byte[len];
            in.readFully(buffer);
        }
        in.close();
        return buffer;
    }

    public static InputStream openStream(final URL url) throws Exception {
        return getInputStream(getConnection(url));
    }

    private static InputStream getInputStream(final URLConnection con) throws Exception {
        final InputStream in = con.getInputStream();
        final String encoding = con.getHeaderField("Content-Encoding");
        if (encoding != null) {
            if (encoding.equalsIgnoreCase("gzip")) {
                return new GZIPInputStream(in);
            } else if (encoding.equalsIgnoreCase("deflate")) {
                return new InflaterInputStream(in);
            }
        }
        return in;
    }

    private static void write(final InputStream in, final OutputStream out) {
        try {
            final byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isZip(final File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            final byte[] m = new byte[4];
            fis.read(m);
            fis.close();
            return (m[0] << 24 | m[1] << 16 | m[2] << 8 | m[3]) == 0x504b0304;
        } catch (final IOException ignored) {
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (final IOException ignored) {
                }
            }
        }
        return false;
    }

    public static void unzip(final File zip, final File directory, final boolean removeZip) {
        if (!isZip(zip)) {
            return;
        }
        try {
            if (!directory.exists()) {
                directory.mkdirs();
            }
            final ZipFile zipFile = new ZipFile(zip);
            final Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = (ZipEntry) entries.nextElement();
                final String name = entry.getName();
                final File file = new File(name);
                if (name.endsWith("/")) {
                    file.mkdirs();
                    continue;
                }
                if (file.getParentFile() != null) {
                    new File(directory + File.separator + file.getParent()).mkdirs();
                }
                final File f = new File(directory + File.separator + file);
                if (!f.exists()) {
                    f.createNewFile();
                }
                final InputStream input = zipFile.getInputStream(entry);
                final FileOutputStream output = new FileOutputStream(f);
                final byte[] bytes = new byte[1024];
                int length;
                while ((length = input.read(bytes)) >= 0) {
                    output.write(bytes, 0, length);
                }
                input.close();
                output.close();
            }
            zipFile.close();
            if (removeZip) {
                zip.delete();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}