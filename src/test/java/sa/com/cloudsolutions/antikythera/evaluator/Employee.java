package sa.com.cloudsolutions.antikythera.evaluator;

public class Employee {
    int id = 100;
    Person p = new Person("Hornblower");

    public static void main(String[] args) {
        Employee patient = new Employee();
        System.out.println(patient);
    }

    @Override
    public String toString() {
        return "Patient id = %d , Name = %s".formatted(id, p.getName());
    }
}
