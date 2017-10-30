
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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.System.out;


public class Main {


    public static void main(String [] args) throws IOException {

        File inputFile = new File("/home/giuliofisso/Scrivania/WhatsApp/classes.dex");
        List<Path> apksInFolder = getAllFiles(new ArrayList<>(), inputFile.toPath());
        int total = apksInFolder.size();
        int useXor = 0;
        int ex = 0;

        for (Path path : apksInFolder) {
            try {
                if (analyse(path.toFile())) {
                    useXor++;
                }
            } catch (Exception e) {
                ex++;
            }
        }
        System.out.println(String.format("tot=%d, useXor=%d, ex=%d", total, useXor, ex));
    }


    private static boolean analyse(File inputFile) throws IOException {
        System.out.println(inputFile.getName());
        DexFile dexFile = DexFileFactory.loadDexFile(inputFile, Opcodes.forApi(22));
        for (ClassDef classDef : dexFile.getClasses()) {
            for (Method method : classDef.getMethods()) {
                MethodImplementation implementation = method.getImplementation();
                if (implementation == null) {
                    continue;
                }
                for (Instruction instruction : implementation.getInstructions()) {
                    switch (instruction.getOpcode()) {
                        case XOR_INT:
                        case XOR_INT_2ADDR:
                        case XOR_INT_LIT8:
                        case XOR_LONG:
                        case XOR_LONG_2ADDR:
                        case XOR_INT_LIT16:
                            return true; // FIXME
                            break;
                        default:
                            continue;
                    }
                    String defAndMeth = method.getDefiningClass() + "->" + method.getName();
                }
            }


        }
    }

    /*
    {
        "nomeapp" : { "defclass->meth" : "xor-int", },
        "nomeapp2" : { },
    }

     */

    private static List<Path> getAllFiles(List<Path> fileNames, Path dir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (path.toFile().isDirectory()) {
                    getAllFiles(fileNames, path);
                } else {
                    if (path.toString().endsWith(".apk"))
                        fileNames.add(path);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }
}
