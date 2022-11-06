package io.jstach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.System.out;

public enum VersionHelper {
    CURRENT() {
        @Override
        public void run(List<String> args) throws IOException {
            var version = current();
            out.println(version.print());
        }
    },
    TRANSFORM() {
        @Override
        public void run(List<String> args) throws IOException {
            var input = Version.of(args.get(0));
        }
    }
    ;

    public abstract void run(List<String> args) throws IOException;


    record Version(int major, int minor, int patch){
        static final Pattern pattern = Pattern.compile("([0-9]+).([0-9]+).([0-9]+)(-SNAPSHOT)?");
        static Version of(String s) {
            Matcher m = pattern.matcher(s);
            if(! m.matches()) {
                throw new IllegalArgumentException(s);
            }
            int major = Integer.parseInt(m.group(1));
            int minor = Integer.parseInt(m.group(2));
            int patch = Integer.parseInt(m.group(3));
            return new Version(major, minor, patch);
        }
        public String print() {
            return major() + "." + minor() + "." + patch();
        }

        public String toString() {
            return print();
        }
    }

    static Version current() throws IOException {
        var props = new Properties();
        props.load(Files.newBufferedReader(Path.of("version.properties")));
        String v  = props.getProperty("version");
        return Version.of(v);
    }

    
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("No command passed");
        }
        VersionHelper helper = VersionHelper.valueOf(args[0].toUpperCase());
        var params = Stream.of(args).skip(1).toList();
        try {
            helper.run(params);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
