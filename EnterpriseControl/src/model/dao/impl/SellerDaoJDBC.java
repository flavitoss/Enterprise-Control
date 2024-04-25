package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {

    private Connection conn;

    public SellerDaoJDBC(Connection conn) {

        this.conn = conn;
    }

    @Override
    public void insert(Seller obj) {

        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("INSERT INTO seller\n" +
                    "(Name, Email, BirthDate, BaseSalary, DepartmentId)\n" +
                    "VALUES\n" +
                    "(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, obj.getName());
            ps.setString(2, obj.getEmail());
            ps.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            ps.setDouble(4, obj.getBaseSalary());
            ps.setInt(5, obj.getDepartment().getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) obj.setId(rs.getInt(1));

                DB.closeResultSet(rs);
            }
            else throw new DbException("Error inserting Seller object");


        } catch (SQLException e) {

            throw new DbException(e.getMessage());
        }
        finally {
            DB.closePreparedStatement(ps);

        }

    }

    @Override
    public void update(Seller obj) {

        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(
                    "UPDATE seller "
                    + "set Name=?,Email=?,BirthDate=?,BaseSalary=?,DepartmentId=? "
                    +"where id=? ");

            ps.setString(1, obj.getName());
            ps.setString(2, obj.getEmail());
            ps.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            ps.setDouble(4, obj.getBaseSalary());
            ps.setInt(5, obj.getDepartment().getId());
            ps.setInt(6,obj.getId());

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new DbException(e.getMessage());
        }
        finally {

            DB.closePreparedStatement(ps);

        }

    }

    @Override
    public void deleteById(Integer id) {

        PreparedStatement ps = null;

        try {

            ps = conn.prepareStatement("DELETE FROM seller WHERE id=? ");
            ps.setInt(1, id);
            ps.executeUpdate();

        }

        catch (SQLException e) {

            throw new DbException(e.getMessage());
        }

        finally {

            DB.closePreparedStatement(ps);
        }
    }
        @Override
    public Seller findById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                       "SELECT seller.*,department.Name as DepName "
                                + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = Department.Id "
                            + "where seller.Id = ?");

            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {

               Department dep = instantiateDepartment(rs);

                return instantiateSeller(rs,dep);

            }

            return null;

        } catch (SQLException e) {

            throw new DbException(e.getMessage());
        }
        finally {

            DB.closePreparedStatement(ps);
            DB.closeResultSet(rs);

        }

    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(

                            "SELECT seller.*,department.Name as DepName "
                                + "FROM seller INNER JOIN department "
                                + "ON seller.DepartmentId = department.Id "
                                + "WHERE DepartmentId = ? "
                                + "order by Name");

            ps.setInt(1, department.getId());
            rs = ps.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> departmentMap = new HashMap<>();

            while (rs.next()) {

                Department dep = departmentMap.get(rs.getInt("DepartmentId"));

                if (dep == null) {

                     dep = instantiateDepartment(rs);
                    departmentMap.put(rs.getInt("DepartmentId"), dep);

                }

                Seller obj = instantiateSeller(rs, dep);
                list.add(obj);

                return list;

            }

            return null;

        } catch (SQLException e) {

            throw new DbException(e.getMessage());
        }
        finally {

            DB.closePreparedStatement(ps);
            DB.closeResultSet(rs);

        }

    }


    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("DepartmentId"));
        dep.setName(rs.getString("DepName"));

        return dep;

    }
    private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
        Seller seller = new Seller();
        seller.setId(rs.getInt("Id"));
        seller.setDepartment(dep);
        seller.setName(rs.getString("Name"));
        seller.setEmail(rs.getString("Email"));
        seller.setBaseSalary(rs.getDouble("BaseSalary"));
        seller.setBirthDate(rs.getDate("BirthDate"));

        return seller;

    }

    @Override
    public List<Seller> findAll() {

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(

                    "SELECT seller.*,department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "order by Name");


            rs = ps.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> departmentMap = new HashMap<>();

            while (rs.next()) {

                Department dep = departmentMap.get(rs.getInt("DepartmentId"));

                if (dep == null) {

                    dep = instantiateDepartment(rs);
                    departmentMap.put(rs.getInt("DepartmentId"), dep);

                }

                Seller obj = instantiateSeller(rs, dep);
                list.add(obj);

                return list;

            }

            return null;

        } catch (SQLException e) {

            throw new DbException(e.getMessage());
        }
        finally {

            DB.closePreparedStatement(ps);
            DB.closeResultSet(rs);

        }
    }
}
