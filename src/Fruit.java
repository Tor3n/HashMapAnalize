class Fruit{
    private String name;
    private double price;

    public Fruit(String name, double price){
        this.name = name;
        this.price = price;
    }

    public String getName(){
        return this.name;
    }

    public double getPrice(){
        return this.price;
    }

    @Override
    public int hashCode(){
        return String.valueOf(name.charAt(0)).hashCode();
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Fruit)){
            return false;
        }

        Fruit oF = (Fruit) o;
        if (this.name.equals(oF.getName()) && this.price == oF.getPrice()) {
            return true;
        }
        return false;
    }
}