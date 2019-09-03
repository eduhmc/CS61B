class Max{
	public static int max(int[] a){
		int temp = a[0];
		for (int i = 0; i < a.length; i++){
			if (a[i]> temp){
				temp = a[i];
			}
		}
		return temp;

	}
	public static void main(String[] args){
		int[] numbers = new int[]{9, 42, 15, 2, 22, 10, 6};
		System.out.println("The largest number of the array is: " + max(numbers));
	}
}