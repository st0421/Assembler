package assembler;

public class Assignment {
	public static void main(String[] args) {

		instructionCycle cpu = new instructionCycle();
		System.out.println("표 6-2 프로그램 동작!!!!!");
		cpu.printCycle();

		cpu = new instructionCycle(1);
		System.out.println("표 6-9 프로그램 동작!!!!!");
		cpu.printCycle();

	}
}
