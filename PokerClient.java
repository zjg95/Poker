import java.io.*;
import java.util.Scanner;

public class PokerClient {

	private static Client server;
	private static Player player;
        private static Scanner sc;
        private static String name;

	public static void main(String[] args){
                System.out.println("---------------------");
		System.out.println("| Poker Client v1.3 |");
		System.out.println("---------------------");

		String hostName = "localhost";
                int portNumber = 4444;

                sc = new Scanner(System.in);

                System.out.print("Enter server name: ");
                hostName = sc.next();

                /*System.out.print("Enter your name: ");
                name = sc.next();*/

                server = new Client(hostName,portNumber);
                //server.tell(name);
                System.out.printf("Connected to server %s\n",server.address);
                player = new Player();

                listen();
	}
        private static void listen(){
                String status;
                game:
                do {
                        status = server.listen();
                        switch (status){
                                case("new"):
                                        new_game();
                                        break;
                                case("kill"):
                                        System.out.println("Exiting game.");
                                        break game;
                                case("draw"):
                                        draw_card();
                                        break;
                                case("bet"):
                                        place_bet();
                                        break;
                                case("askbet"):
                                        ask_bet();
                                        break;
                                case("printhand"):
                                        player.printHand();
                                        System.out.println();
                                        break;
                                case("move"):
                                        move();
                                        break;
                                case("lose"):
                                        game_lose();
                                        break;
                                case("yesno"):
                                        yes_no();
                                        break;
                                case("pay"):
                                        collect_pay();
                                        break;
                                default: System.out.println(status);
                        }
                } while (true);
        }
        private static void collect_pay(){
                System.out.println("                      You fucking won!");
                player.wins++;
                int winnings = Integer.parseInt(server.listen());
                System.out.printf("                      You collect $%d!\n",winnings);
                player.addMoney(winnings);
        }
        private static void new_game(){
                System.out.println();
                System.out.println();
                System.out.println("New Game");
                int n = Integer.parseInt(server.listen());
                player.reset();
        }
        private static void draw_card(){
                Card card = new Card(Integer.parseInt(server.listen()),Integer.parseInt(server.listen()));
                player.draw(card);
        }
        private static void place_bet(){
                boolean bool = (Integer.parseInt(server.listen())>0)?true:false;
                int amount = Integer.parseInt(server.listen());
                player.bet(amount,bool);
                System.out.printf("Betting $%d\n",amount);
                System.out.printf("You have $%d remaining\n",player.money);
        }
        private static void game_lose(){
                System.out.print("                      ");     
                System.out.println("YOU FUCKING LOST!");
                int winner = Integer.parseInt(server.listen());
                System.out.print("                      ");     
                System.out.printf("Winning hand (Player %d):\n",winner);
                System.out.print("                      ");     
                for (int i = 0; i<5; i++){
                        Card c = new Card(Integer.parseInt(server.listen()),Integer.parseInt(server.listen()));
                        System.out.printf("%s ",c);
                }
                player.losses++;
                System.out.println();
                System.out.print("                      ");     
                System.out.println(server.listen());
        }
        private static void yes_no(){
                System.out.println("Message from server:");
                String str = server.listen();
                System.out.printf("'%s'\n",str);
                do {
                        System.out.print("Respond (y/n): ");
                        str = sc.next();
                } while (!(str.equals("y")||str.equals("n")));
                server.tell(str);
        }
        private static void move(){
                System.out.print("What will you swap (type 'done' when done)? ");
                String response;
                while (!((response=sc.next()).equals("done"))){
                        if (response.equals("all")){
                                for (int i = 0; i<5; i++){
                                        server.tell(""+i);
                                        player.discard(i);
                                }
                                server.tell("done");
                                return;
                        }
                        server.tell(response);
                        player.discard(Integer.parseInt(response));
                }
                server.tell(response);
        }
        private static void ask_bet(){
                int minimum_bet = Integer.parseInt(server.listen())-player.current_bet;
                System.out.printf("The current minimum bet is $%d\n",minimum_bet);
                String response = null;
                int bet = -1;
                System.out.print("How much will you bet? ");
                while (bet<minimum_bet){       
                        System.out.printf("You must bet at least $%d or fold! ",minimum_bet);
                        response = sc.next();
                        if (response.equals("fold")){
                                System.out.println("You fold.");
                                player.fold();
                                server.tell(response);
                                return;
                        }
                        bet = Integer.parseInt(response);
                        if (!player.canBet(bet)){
                                System.out.printf("You cannot afford to bet %d because you only have $%d!\n",bet,player.money);
                                bet=0;
                        }

                }
                player.bet(bet,true);
                server.tell(response);
        }
}