/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.db;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Ehbb Abdel Mawla
 */
public abstract class DBCRUD<E> extends RecursiveTreeObject<E> {

    ArrayList<DBField> fields = new ArrayList<>();
    String tableName;
    private List<DBField> updatedFields;
    private List<DBField> craiteriaFields;

    /**
     * @param tableName table name To Deal With
     */
    public DBCRUD(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Getters and Setters
     *
     * @return map of data
     */
    public ArrayList<DBField> getFields() {
        return fields;
    }

    public void setFields(ArrayList<DBField> fields) {
        this.fields = fields;
        updatedFields = fields.stream()
                .filter(field -> !(field.getDbConstraint().equals("PK") || field.getDbConstraint().equals("UNIQUE")))
                .collect(Collectors.toList());
        craiteriaFields = fields.stream()
                .filter(field -> (field.getDbConstraint().equals("PK") || field.getDbConstraint().equals("UNIQUE")))
                .collect(Collectors.toList());

    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     *
     * @return the basic array that map data with tables
     */
    public abstract ArrayList<DBField> createMap();

    /**
     * DataBase Basic Operations
     */
    /**
     * Add object TO DB
     *
     * @return flag Integer: 1 => Success -2 => Duplicate Constraint -5 || 0 =>
     * UnCatched Exception
     */
    public int add() {
        try {
            PreparedStatement stat = DatabaseHandler.con.prepareStatement("INSERT INTO " + tableName + " " + getAddString());

            for (int i = 0; i < fields.size(); i++) {
//                System.out.println(fields.get(i).getDbfieldName() + " = " + fields.get(i).getVal());
                if (fields.get(i).getVal().getClass().equals(String.class)) {
                    stat.setString(i + 1, (String) fields.get(i).getVal());
                } else if (fields.get(i).getVal().getClass().equals(Integer.class)) {
                    stat.setInt(i + 1, (Integer) fields.get(i).getVal());
                } else if (fields.get(i).getVal().getClass().equals(Double.class)) {
                    stat.setDouble(i + 1, (Double) fields.get(i).getVal());
                } else if (fields.get(i).getVal().getClass().equals(Float.class)) {
                    stat.setFloat(i + 1, (Float) fields.get(i).getVal());
                }
            }
            stat.execute();
            return 1;
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
//                System.out.println(" SQLException 1 add() " + e);
                return -2;
            }
//            System.out.println(" SQLException 2 add() " + e);
            return 0;
        } catch (Exception e) {
//            System.out.println(" Exception add() " + e);
            return -5;
        }

    }

    /**
     * @return Complete SQL Insert Statement
     */
    private String getAddString() {
        StringBuilder fieldsNames = new StringBuilder();
        StringBuilder fieldsPlaceHolders = new StringBuilder();
        fieldsNames.append("(");
        fieldsPlaceHolders.append("(");
        for (int i = 0; i < fields.size(); i++) {
            if ((fields.size() - 1) == i) {
                fieldsNames.append(fields.get(i).getDbfieldName());
                fieldsPlaceHolders.append("?");
                continue;
            }
            fieldsPlaceHolders.append("?,");
            fieldsNames.append(fields.get(i).getDbfieldName()).append(",");
        }
        fieldsNames.append(") VALUES ");
        fieldsPlaceHolders.append(")");

        return fieldsNames.toString() + fieldsPlaceHolders.toString();
    }

    /**
     * UPDTAE object from DB
     *
     * @return flag Integer: 1 => Success -2 => Duplicate Constraint -5 || 0 =>
     * UnCatched Exception
     */
    public int update() {
        try {
            StringBuilder fieldsNames = new StringBuilder();
            /**
             * fill Fields That will be Update 'Not Unique or PK'
             */
            for (int i = 0; i < updatedFields.size(); i++) {
                if ((updatedFields.size() - 1) == i) {
                    fieldsNames.append(updatedFields.get(i).getDbfieldName()).append("=?");
                    continue;
                }
                fieldsNames.append(updatedFields.get(i).getDbfieldName()).append("=?,");
            }
            if (!craiteriaFields.isEmpty()) {
                fieldsNames.append(" WHERE ");
            }
            /**
             * fill craiteriaFields
             */
            for (int i = 0; i < craiteriaFields.size(); i++) {
                if ((craiteriaFields.size() - 1) == i) {
                    fieldsNames.append(craiteriaFields.get(i).getDbfieldName()).append("=?");
                    continue;
                }
                fieldsNames.append(craiteriaFields.get(i).getDbfieldName()).append("=? AND ");
            }

//            System.out.println("UPDATE " + tableName + " SET " + fieldsNames.toString());
            PreparedStatement stat = DatabaseHandler.con.prepareStatement("UPDATE " + tableName + " SET " + fieldsNames.toString());
            /**
             * Set Updated Fields
             */
            for (int i = 0; i < updatedFields.size(); i++) {

                if (updatedFields.get(i).getVal().getClass().equals(String.class)) {
//                    System.out.println(i + 1 + " " + updatedFields.get(i).getVal());
                    stat.setString(i + 1, (String) updatedFields.get(i).getVal());
                } else if (updatedFields.get(i).getVal().getClass().equals(Integer.class)) {
//                    System.out.println(i + 1 + " " + updatedFields.get(i).getVal());
                    stat.setInt(i + 1, (Integer) updatedFields.get(i).getVal());
                } else if (updatedFields.get(i).getVal().getClass().equals(Double.class)) {
//                    System.out.println(i + 1 + " " + updatedFields.get(i).getVal());
                    stat.setDouble(i + 1, (Double) updatedFields.get(i).getVal());
                } else if (updatedFields.get(i).getVal().getClass().equals(float.class)) {
//                    System.out.println(i + 1 + " " + updatedFields.get(i).getVal());
                    stat.setFloat(i + 1, (Float) updatedFields.get(i).getVal());
                }
            }
            /**
             * Set craiteriaFields
             */
            int counter = updatedFields.size() + 1;
            for (int i = 0; i < craiteriaFields.size(); i++, counter++) {
                if (craiteriaFields.get(i).getVal().getClass().equals(String.class)) {
//                    System.out.println(counter + " " + craiteriaFields.get(i).getVal());
                    stat.setString(counter, (String) craiteriaFields.get(i).getVal());
                } else if (craiteriaFields.get(i).getVal().getClass().equals(Integer.class)) {
//                    System.out.println(counter + " " + craiteriaFields.get(i).getVal());
                    stat.setInt(counter, (Integer) craiteriaFields.get(i).getVal());
                } else if (craiteriaFields.get(i).getVal().getClass().equals(Double.class)) {
//                    System.out.println(counter + " " + craiteriaFields.get(i).getVal());
                    stat.setDouble(counter, (Double) craiteriaFields.get(i).getVal());
                } else if (craiteriaFields.get(i).getVal().getClass().equals(Float.class)) {
//                    System.out.println(counter + " " + craiteriaFields.get(i).getVal());
                    stat.setFloat(counter, (Float) craiteriaFields.get(i).getVal());
                }
            }
            stat.execute();
            return 1;
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
//                System.out.println(" SQLException update() " + e);
                return -2;
            }
            return 0;
        } catch (Exception e) {
//            System.out.println(" Exception update() " + e);
            return -5;
        }

    }

    /**
     * Remove Record
     *
     * @return flag Integer: 1 => Success -5 => UnCatched Exception
     */
    public int remove() {
        try {
            StringBuilder fieldsNames = new StringBuilder();
            fieldsNames.append(" WHERE ");
            for (int i = 0; i < craiteriaFields.size(); i++) {
                if ((craiteriaFields.size() - 1) == i) {
                    fieldsNames.append(craiteriaFields.get(i).getDbfieldName()).append("=?");
                    continue;
                }
                fieldsNames.append(craiteriaFields.get(i).getDbfieldName()).append("=? AND ");
            }

            PreparedStatement stat = DatabaseHandler.con.prepareStatement("DELETE FROM " + tableName + fieldsNames.toString());
            for (int i = 0; i < craiteriaFields.size(); i++) {
                if (craiteriaFields.get(i).getVal().getClass().equals(String.class)) {
                    stat.setString(i + 1, (String) craiteriaFields.get(i).getVal());
                } else if (craiteriaFields.get(i).getVal().getClass().equals(Integer.class)) {
                    stat.setInt(i + 1, (Integer) craiteriaFields.get(i).getVal());
                } else if (craiteriaFields.get(i).getVal().getClass().equals(Double.class)) {
                    stat.setDouble(i + 1, (Double) craiteriaFields.get(i).getVal());
                } else if (craiteriaFields.get(i).getVal().getClass().equals(Float.class)) {
                    stat.setFloat(i + 1, (Float) craiteriaFields.get(i).getVal());
                }
            }
            stat.execute();
            return 1;
        } catch (Exception e) {
//            System.out.println(" Exception remove() " + e);
            return -5;
        }

    }

//    @Override
//    public String toString() {
//        String str = "";
//        for (int i = 0; i < fields.size(); i++) {
//            str += fields.get(i).getDbfieldName() + " = " + fields.get(i).getVal().toString() + "\n";
//        }
//        return str;
//    }
    /**
     * GET All Record
     *
     * @return flag Integer: ResultSet obj => Success null => UnCatched
     * Exception
     */
//    public  ResultSet getAllData(){
//        try{
//            ResultSet res=con.prepareStatement("SELECT * FROM "+tableName).executeQuery();
//            return res;
//        }
//        catch (Exception e){
//        return null;
//        }
//    }
}
