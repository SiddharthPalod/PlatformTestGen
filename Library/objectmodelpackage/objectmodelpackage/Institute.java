

//Abstract Class used
//Below use of abstract class to achieve abstaraction however the purpose of both interface and abstract classes are same 
public abstract class Institute {
    private String instituteName;

    //constructor used for initializing
    
    public Institute() {
    }

    Institute(String instituteName) {
        this.instituteName = instituteName;
    }

    public String getInstituteName() {
        return instituteName;
    }
}