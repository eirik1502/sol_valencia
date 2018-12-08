package tryingStuff;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by eirik on 24.11.2018.
 */
public class QualifiedFromSimpleClassName {

    public static void main(String[] args) {
        QualifiedFromSimpleClassName c = new QualifiedFromSimpleClassName();

        //Collection<String> packages = c.getPackages();
        //System.out.println(packages);

        String classpath = System.getProperty("java.class.path");
        System.out.println(classpath);
        try {
            Class<?> clazz = Class.forName("PositionComp");
            System.out.println(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public Collection<String> getCurrentClassloaderPackages() {
        Set<String> packages = new HashSet<>();
        for (Package aPackage : Package.getPackages()) {
            packages.add(aPackage.getName());
        }
        return packages;
    }

//    public Collection<String> getPackages() {
//        String classpath = System.getProperty("java.class.path");
//        return getPackageFromClassPath(classpath);
//    }

//    public static Set<String> getPackageFromClassPath(String classpath) {
//        Set<String> packages = new HashSet<String>();
//        String[] paths = classpath.split(File.pathSeparator);
//        for (String path : paths) {
//            if (path.trim().length() == 0) {
//                continue;
//            } else {
//                File file = new File(path);
//                if (file.exists()) {
//                    String childPath = file.getAbsolutePath();
//                    if (childPath.endsWith(".jar")) {
//
//                        packages.addAll(ClasspathPackageProvider
//                                .readZipFile(childPath));
//                    } else {
//                        packages.addAll(ClasspathPackageProvider
//                                .readDirectory(childPath));
//                    }
//                }
//            }
//
//        }
//        return packages;
//    }
}
