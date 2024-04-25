package application;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.util.List;

public class Program2 {
    public static void main(String[] args) {

        DepartmentDao departmentDao = DaoFactory.createDepartmentDao();

        System.out.println("=== TEST 1: Department findById ===");
        Department department = departmentDao.getById(3);
        System.out.println(department);

        System.out.println("=== TEST 2: Department findAll ===");

        List<Department> list;
        list = departmentDao.getAll();
        list.forEach(System.out::println);

        System.out.println("=== TEST 3: Department insert ===");

        Department department1 = new Department(null,"guitars");
        departmentDao.insert(department1);
        System.out.println("Inserted! new id: " + department1.getId());

        System.out.println("=== TEST 4: Department update ===");

        departmentDao.getById(1);
        department.setName("notebooks");
        departmentDao.update(department);
        System.out.println("Updated!");

        System.out.println("=== TEST 5: Department delete ===");

        departmentDao.deleteById(2);
        System.out.println("Deleted!");
    }
}
