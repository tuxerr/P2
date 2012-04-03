import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class StubGenerator {
    public SharedObject generateStubFromObject(Object o,int i) {
        String stubname=o.getClass().getName()+"_stub";
        String stubfilename = stubname+".java";
        SharedObject so=null;

        if(!(new File(stubfilename)).exists()) {
            createStubFileFromObject(o);
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            compiler.run(null,null,null,stubfilename);
        }

        Class<?> c;
        Constructor<?> cons;
        try {
            c = Class.forName(stubname);
            cons = c.getConstructor(new Class[] {Object.class,int.class});
            so = (SharedObject) cons.newInstance(new Object[] {o,i});
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return so;
    }

    private void createStubFileFromObject(Object o) {
        // l'objet o de nom de classe C doit avoir une interface de type C_itf.
        String classname = o.getClass().getName();
        String interfacename = classname + "_itf";
        String stubname = classname + "_stub";

        try {
            File file = new File(stubname+".java");
            FileWriter writer = new FileWriter(file);

            //generate headers 
            writer.write("public class " + stubname + " extends SharedObject implements " + interfacename + " ,java.io.Serializable { \n");

            //generate constructor will be the SharedObject one, SharedObject(Object obj, int i) {
            writer.write("\tpublic "+stubname+"(Object obj,int i) {\n\t\tsuper(obj,i);\n\t}\n\n");
            
            //generate methods
            Class<?> inter_class = Class.forName(interfacename);
            Method[] inter_methods = null;
            inter_methods = inter_class.getMethods();

            for (Method m : inter_methods) {
                StringBuilder sb = new StringBuilder();
                int methodmodifiers = m.getModifiers();
                sb.append("\tpublic");

                //method modifiers
                if(Modifier.isFinal(methodmodifiers)) {
                    sb.append(" final");
                }
                if(Modifier.isStatic(methodmodifiers)) {
                    sb.append(" static");
                }
                if(Modifier.isSynchronized(methodmodifiers)) {
                    sb.append(" static");
                }
                
                //method name and return types
                boolean returnisvoid = m.getReturnType().getName().equals("void");
                sb.append(" " + m.getReturnType().getName());

                sb.append(" " + m.getName()+ "(");
                

                //method parameters
                int num=0;
                for(Class c : m.getParameterTypes()) {
                    if(num!=0) {
                        sb.append(", ");
                    }
                    sb.append(c.getName()+ " a" + num);
                    num++;
                }

                sb.append(")");
                if(m.getExceptionTypes().length > 0) {
                    sb.append(" throws");
                }
                
                // method exceptions
                num=0;
                for(Class c : m.getExceptionTypes()) {
                    if(num!=0) {
                        sb.append(", ");
                    }
                    sb.append(" " + c.getName());
                    num++;
                }
                

                //method content
                sb.append(" {\n");
                sb.append("\t\t" + classname + " o = (" + classname + ") obj; \n");

                sb.append("\t\t");
                if(!returnisvoid) {
                    sb.append("return ");
                }
                sb.append("o."+m.getName()+"(");
                num=0;
                for(Class c : m.getParameterTypes()) {
                    if(num!=0) {
                        sb.append(",");
                    }
                    sb.append("a"+num);
                    num++;
                }
                sb.append(");\n");
                
                sb.append("\t}\n\n");
                writer.write(sb.toString());                
            }


            writer.write("\n}\n");
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}