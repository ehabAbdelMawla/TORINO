/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.db;

/**
 *
 * @author Ehab Abdel Mawla
 */
public class DBField<E> {

    E val;
    String dbfieldName;
    String dbConstraint;

    /**
     *
     * @param val value of dbField and Type
     * @param dbfieldName name of Field in dataBase Table
     * @param dbConstraint Constraint of column [PK,NN,UNIQUE]
     */
    public DBField(E val, String dbfieldName, String dbConstraint) {
        this.val = val;
        this.dbfieldName = dbfieldName;
        this.dbConstraint = dbConstraint;
    }

    public E getVal() {
        return val;
    }

    public void setVal(E val) {
        this.val = val;
    }

    public String getDbConstraint() {
        return dbConstraint;
    }

    public void setDbConstraint(String dbConstraint) {
        this.dbConstraint = dbConstraint;
    }

    public String getDbfieldName() {
        return dbfieldName;
    }

    public void setDbfieldName(String dbfieldName) {
        this.dbfieldName = dbfieldName;
    }

    @Override
    public String toString() {
        return "DBField{" + "val=" + val + ", dbfieldName=" + dbfieldName + ", dbConstraint=" + dbConstraint + "}\n";
    }
}
