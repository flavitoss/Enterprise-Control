package model.dao.impl;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentDaoJDBC implements DepartmentDao {

    private Connection conn;

    public DepartmentDaoJDBC(Connection conn) {

        this.conn = conn;
    }

    @Override
    public void insert(Department obj) {

        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(
                    "INSERT INTO department "
                        + "(Name) "
                        + "VALUES "
                        + "(?)", Statement.RETURN_GENERATED_KEYS);

            ps.setString(1,obj.getName());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) obj.setId(rs.getInt(1));

                DB.closeResultSet(rs);
            }
            else throw new DbException("Error inserting object");

        } catch (SQLException e) {

           throw new DbException(e.getMessage());
        }
        finally {
            DB.closePreparedStatement(ps);
        }
    }

    @Override
    public void update(Department obj) {
        PreparedStatement ps = null;
        try {

            ps = conn.prepareStatement("UPDATE department set Name=? where id=?");
            ps.setString(1,obj.getName());
            ps.setInt(2,obj.getId());

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

            ps = conn.prepareStatement("DELETE from department WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {

            throw new DbIntegrityException(e.getMessage());
        }
        finally {

            DB.closePreparedStatement(ps);
        }

    }

    @Override
    public Department getById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT * from department WHERE Id=?");
            ps.setInt(1,id);
            rs = ps.executeQuery();

            if (rs.next()){

                Department obj = instantiateDepartment(rs);
                obj.setId(rs.getInt("Id"));
                obj.setName(rs.getString("Name"));
                return obj;
            }
            return null;
        }
        catch (SQLException e) {

            throw new DbException(e.getMessage());
        }
        finally {

            DB.closePreparedStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Department> getAll() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT  * FROM department ORDER BY Name");
            rs = ps.executeQuery();
            List<Department> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()){

                Department dep = map.get(rs.getInt("Id"));

                if (dep == null){

                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("Id"),dep);
                    list.add(dep);
                }
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
        dep.setId(rs.getInt("Id"));
        dep.setName(rs.getString("Name"));

        return dep;

    }
}
