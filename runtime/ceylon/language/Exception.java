package ceylon.language;

import com.redhat.ceylon.compiler.java.TypeDescriptor;
import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Class;
import com.redhat.ceylon.compiler.java.metadata.Defaulted;
import com.redhat.ceylon.compiler.java.metadata.Ignore;
import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;

@Ceylon(major = 4)
@Class(extendsType = "ceylon.language::Basic")
public class Exception extends RuntimeException {

    @Ignore
    public final static TypeDescriptor TYPE_DESCRIPTOR = TypeDescriptor.klass(Exception.class);

	private static final long serialVersionUID = -1790691559137471641L;

	private String description;
    
    public Exception(
            @TypeInfo("ceylon.language::String|ceylon.language::Null")
            @Name("description")
            @Defaulted
            String description,
            @TypeInfo("ceylon.language::Exception|ceylon.language::Null")
            @Name("cause")
            @Defaulted
            Throwable cause) {
        super(description==null ? null : description.toString(), cause);
        this.description = description;
    }
    
    @Ignore
    public Exception(String description) {
        this(description, $init$cause(description));
    }
    
    @Ignore
    public Exception() {
        this($init$description());
    }
        
    @TypeInfo("ceylon.language::Exception|ceylon.language::Null")
    public Throwable getCause() {
        return super.getCause();
    }
    
    @TypeInfo("ceylon.language::String")
    public java.lang.String getMessage() {
        if (description != null
                && description != null) {
            return description.toString();
        } 
        else if (getCause() != null 
                && getCause().getMessage() != null) {
            return getCause().getMessage();
        }
        return "";
    }

    @TypeInfo("ceylon.language::String")
    public java.lang.String toString() {
        return className_.className(this) + " \"" + getMessage() +"\""; 
    }
    
    @Override
    public void printStackTrace() {
    	super.printStackTrace();
    }

    @Ignore
    public static String $init$description(){
        return null;
    }
    @Ignore
    public static Throwable $init$cause(String description){
        return null;
    }
}
