
/*
* Software sviluppato da Giulio Tavella.
* Importante inserire dentro al gragle:
*     compile 'com.github.lanchon.dexpatcher:multidexlib2:2.2.1.r2'

* */




import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.immutable.ImmutableClassDef;
import org.jf.dexlib2.immutable.ImmutableMethod;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.System.out;


public class Main {



    public static void main(String [] args) throws IOException {
        File inputFile = new File("/home/giuliofisso/Scrivania/WhatsApp/classes.dex");

        DexFile dexFile = DexFileFactory.loadDexFile(inputFile, Opcodes.forApi(22));
        for (ClassDef classDef : dexFile.getClasses()) {
            ClassDef d = customizeClass(classDef);


        }
    }

    private static ClassDef customizeClass(ClassDef classDef) {
        List<Method> methods = new ArrayList<>();
        boolean modifiedMethod = false;
        for (Method method : classDef.getMethods()) {
            //out.println("DIO->" + method.getName());
            MethodImplementation implementation = method.getImplementation();
            if (implementation == null) {
                //out.println("METODO1 : " + method.getName());
                methods.add(method);
                continue;
            }
            MethodImplementation customImpl = searchAndReplaceInvocations(implementation);
            if (customImpl==implementation) {
                //out.println("METODO2 : " + method.getName());

                methods.add(method);
                continue;
            }
            modifiedMethod = true;
            final ImmutableMethod newMethod = new ImmutableMethod(method.getDefiningClass(),
                    method.getName(),
                    method.getParameters(),
                    method.getReturnType(),
                    method.getAccessFlags(),
                    method.getAnnotations(),
                    customImpl);
            //out.println("METODO3 : " + newMethod.getName());

            methods.add(newMethod);
        }
        if (!modifiedMethod)
            return classDef;
        return new ImmutableClassDef(classDef.getType(),
                classDef.getAccessFlags(),
                classDef.getSuperclass(),
                classDef.getInterfaces(),
                classDef.getSourceFile(),
                classDef.getAnnotations(),
                classDef.getFields(),
                methods);
    }


    /*
    * Questa funzione prende tutti metodi utilizzati e controllo se sono
    * chiamate a sistema, se sono chiamate a sistema allora appartengono a questa classe
    * com/android|android|com/google|javax?|dalvik|org/apache
    *
    * */

    private static MethodImplementation searchAndReplaceInvocations(MethodImplementation origImplementation) {
        MutableMethodImplementation newImplementation = null;
        int i = -1;
        for (Instruction instruction : origImplementation.getInstructions()) {

            System.out.println(instruction.getOpcode().name + " " + instruction.getOpcode().flags );
            // Controllo che instruction.getOpcode().flags sia uno di quelli che sono scritti qui sotto, Corretto ?




            /*
            * SOlo le istruzioni con lo xor sono quelle che prendo:
            * Istruzione->xor-long/2addr  opCode->52
            * Istruzione->xor-int/lit8 opCode-> 20
            *
            * Da intenet sul sito di android ho questo:
            * 97: xor-int
            * */

        }
        return newImplementation!=null ? newImplementation : origImplementation;
    }


}
