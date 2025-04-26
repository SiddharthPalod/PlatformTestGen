package sidlibrary.objectmodelpackage;

public abstract class Institute {
    private String instituteName;

    public Institute() {}

    public Institute(String instituteName) {
        this.instituteName = instituteName;
    }

    public String getInstituteName() {
        return instituteName;
    }
}
