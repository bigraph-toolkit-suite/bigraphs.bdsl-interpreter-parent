package org.bigraphs.dsl.interpreter.udf;

import org.bigraphs.dsl.udf.BDSLUserDefinedConsumer;
import org.bigraphs.dsl.udf.BDSLUserDefinedFunction;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @param <C>
 * @author Dominik Grzelak
 * @see <a href="https://stackabuse.com/example-loading-a-java-class-at-runtime/">Source: https://stackabuse.com/example-loading-a-java-class-at-runtime/</a>
 */
public class BDSLUserDefinedExtensionLoader<C extends BDSLUserDefinedConsumer & BDSLUserDefinedFunction> {
    String springBootPathPrefix = "/BOOT-INF/classes/";

    public C LoadClass(String directory, String classpath, String className, Class<C> parentClass) throws ClassNotFoundException {
        File pluginsDir = new File(directory); //System.getProperty("user.dir")
        for (File jar : pluginsDir.listFiles()) {
            try {
                URL[] urls = {
                        new URL("jar:file:" + jar.getAbsolutePath() + "!" + springBootPathPrefix),
                        new URL("jar:file:" + jar.getAbsolutePath() + "!/")
                };
                ClassLoader loader = URLClassLoader.newInstance(
//                        new URL[]{jar.toURL()},
                        urls,
                        getClass().getClassLoader()
                );

                List<Class> allClasses = findAllClasses(classpath, loader);
                String fqn = classpath + "." + className;
                Class<?> clazz = Class.forName(fqn, true, loader);
                Class<? extends C> newClass = clazz.asSubclass(parentClass);
                // Apparently its bad to use Class.newInstance, so we use
                // newClass.getConstructor() instead
                Constructor<? extends C> constructor = newClass.getConstructor();
                return constructor.newInstance();

            } catch (ClassNotFoundException e) {
                // There might be multiple JARs in the directory,
                // so keep looking
                continue;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        throw new ClassNotFoundException("Class " + classpath
                + " wasn't found in directory " + System.getProperty("user.dir") + directory);
    }

    public Resource[] scan(ClassLoader loader, String packageName) throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                loader);
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(packageName) + "/**/*.class";
        Resource[] resources = resolver.getResources(pattern);
        return resources;
    }

    public List<Class> findAllClasses(String packageName, ClassLoader loader) throws ClassNotFoundException {
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
                loader);
        List<Class> clazzes = new ArrayList<>();
        try {
            Resource[] resources = scan(loader, packageName);
            for (Resource resource : resources) {
                MetadataReader reader = metadataReaderFactory.getMetadataReader(resource);
                Class<?> aClass = ClassUtils.forName(reader.getClassMetadata().getClassName(), loader);
                clazzes.add(aClass);
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        return clazzes;
    }
}
