import java.io.Serializable;

/**
 * @Class: ClassDemo
 * @Author: chaos
 * @Date: 2019/5/11 15:47
 * @Version 1.0
 */
public class ClassDemo extends Object implements Serializable {

    public static final int CONST_INT = 1;
    public static final float CONST_FLOAT = 1F;
    public static final long CONST_LONG = 1L;
    public static final double CONST_DOUBLE = 1D;

    public static final Integer CONST_INTEGER = 2;
    public static final String CONST_STR = "const_str";

    public Integer publicField ;

    private Integer privateField ;

    public Integer getPublicField() {
        return this.publicField + this.privateField;
    }

    private void setPublicField() {
        this.publicField = 1;
        this.privateField = 1;
    }
}
