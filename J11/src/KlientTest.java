class Main {
	public static void main(String[] args) {
		var client = new Klient();
		client.password("1000");
		client.connect("172.30.24.12", 9090);
	}
}
