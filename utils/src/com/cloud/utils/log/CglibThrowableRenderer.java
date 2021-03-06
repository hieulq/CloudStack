// Copyright 2012 Citrix Systems, Inc. Licensed under the
// Apache License, Version 2.0 (the "License"); you may not use this
// file except in compliance with the License.  Citrix Systems, Inc.
// reserves all rights not expressly granted by the License.
// You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
// Automatically generated by addcopyright.py at 04/03/2012
package com.cloud.utils.log;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.log4j.spi.ThrowableRenderer;

/**
 * This renderer removes all the Cglib generated methods from the call
 * 
 * Unfortunately, I had to copy out the EnhancedThrowableRenderer from
 * the apach libraries because EnhancedThrowableRenderer is a final class.
 * simply override doRender. Not sure what the developers are thinking there
 * making it final.
 * 
 * <throwableRenderer class="com.cloud.utils.log.CglibThrowableRenderer"/>
 * into log4j.xml.
 * 
 */
public class CglibThrowableRenderer implements ThrowableRenderer {
    /**
     * Throwable.getStackTrace() method.
     */
    private Method getStackTraceMethod;
    /**
     * StackTraceElement.getClassName() method.
     */
    private Method getClassNameMethod;

    /**
     * Construct new instance.
     */
    public CglibThrowableRenderer() {
        try {
            Class[] noArgs = null;
            getStackTraceMethod = Throwable.class.getMethod("getStackTrace", noArgs);
            Class ste = Class.forName("java.lang.StackTraceElement");
            getClassNameMethod = ste.getMethod("getClassName", noArgs);
        } catch (Exception ex) {
        }
    }

    @Override
    public String[] doRender(final Throwable th) {
        try {
            ArrayList<String> lines = new ArrayList<String>();
            Throwable throwable = th;
            lines.add(throwable.toString());
            int start = 0;
            do {
                StackTraceElement[] elements = throwable.getStackTrace();
                for (int i = 0; i < elements.length - start; i++) {
                    StackTraceElement element = elements[i];
                    String filename = element.getFileName();
                    String method = element.getMethodName();
                    if ((filename != null && filename.equals("<generated>")) || (method != null && method.equals("invokeSuper"))) {
                        continue;
                    }
                    lines.add("\tat " + element.toString());
                }
                if (start != 0) {
                    lines.add("\t... " + start + " more");
                }
                throwable = throwable.getCause();
                if (throwable != null) {
                    lines.add("Caused by: " + throwable.toString());
                    start = elements.length - 1;
                }
            } while (throwable != null);
            return lines.toArray(new String[lines.size()]);
        } catch (Exception ex) {
            PrintWriter pw = new PrintWriter(System.err);
            ex.printStackTrace(pw);
            pw = new PrintWriter(System.out);
            ex.printStackTrace(pw);
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Find class given class name.
     * 
     * @param className class name, may not be null.
     * @return class, will not be null.
     * @throws ClassNotFoundException thrown if class can not be found.
     */
    private Class findClass(final String className) throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e1) {
                return getClass().getClassLoader().loadClass(className);
            }
        }
    }

}
