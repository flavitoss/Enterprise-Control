package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    }

    @Override
    public void update(Seller obj) {

    }

    @Override
    public void delete(Seller obj) {

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
