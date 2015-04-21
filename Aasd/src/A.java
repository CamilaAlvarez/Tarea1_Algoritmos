
public class A {
	public int a = 2;
	public B b = new B();
	
	int geta(){
		return b.geta();
	}
	
	public static void main(String[] args) {
		A a = new A();
		System.out.println(a.geta());
		
	}
	
}
