package assembler;
/*
 *  어셈블러 PPT 파일에 조각난 코드들을 모아봤습니다.
 *  메모리 입력, T0, T1, T2가 구현되어있습니다.
 *  T2 이후는 구현되지 않았기에 PPT의 결과값과 차이가 있습니다.
 *  HLT가 구현되지 않았습니다!
 *  
 *  구현이 필요한 기능은 다음과 같습니다.
 *  1. asm 파일을 읽어서 setMemory를 진행하는 기능(필요시).
 *  2. instructionCheck(), 즉 T3이후의 동작을 진행하는 기능.
 *  3. 출력 형태 다듬기(필요시)
 * 
 * */
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
