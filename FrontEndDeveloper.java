// --== CS400 File Header Information ==--
// Name: Luke Kolder
// Email: lkolder@wisc.edu
// Team: NG
// Role: Front End Developer
// TA: Daniel Finer
// Lecturer: Florian Heimrl
// Notes to Grader: My part of the project

import java.util.Scanner;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;

public class FrontEndDeveloper {
	private final static String WELCOME = "======== WELCOME to the Delivery Route Applcation ========";
	private final static String GOODBYE = "======== Thank you for using the Delivery Route Application! ========";
	private final static String PROMPT_COMMAND = "\n\nENTER A COMMAND: ";
	private final static String MENU = "\nCOMMAND MENU:\n" + "[R] Create a route based on the entered destiantions.\n"
			+ "[S] Shows the most recently created route. \n" + "[C] Cancels the most recently created route.\n"
			+ "[D] Shows the number of rest stops in each city on the route.\n" + "[M] Display the help menu again. \n"
			+ "[Q] Quit program.";

	private static BackEndDeveloper backEnd = new BackEndDeveloper();
	private static DataWrangler dataWrangler = new DataWrangler();
	private static CityType[] route = new CityType[0];
	private static String state = "";

	/**
	 * This recursive method takes a scanner and prompts the user for the state that
	 * their deliveries are in. It will continue to run until the user enters a
	 * valid state.
	 * 
	 * @param scnr
	 * @return the valid state the user entered
	 */
	public static String enterState(Scanner scnr) {

		System.out.print("Enter the state of your destinations: ");
		String state = scnr.next();
		if (!backEnd.validState(state)) {
			System.out.print("Please enter a valid state.\n" + "(Wisconsin, Minnesota, or Illinois)\n");
			state = enterState(scnr);

		}
		return state;
	}

	/**
	 * Loads the data of the desired state from a txt file.
	 * 
	 * @param state
	 * @throws Exception
	 */
	public static void createGraph(String state) throws Exception {
		if (state.equals("Illinois"))
			dataWrangler.readFile("IllinoisCities.txt");

		if (state.equals("Wisconsin"))
			dataWrangler.readFile("WisconsinCities.txt");

		if (state.contentEquals("Minnesota"))
			dataWrangler.readFile("MinnesotaCities.txt");
	}

	/**
	 * This recursive method prompts the user to enter a city, and will continue to
	 * ask until the user enters a valid city.
	 * 
	 * @param scnr
	 * @return a valid city
	 */
	public static String enterCity(Scanner scnr) {

		String city = scnr.next();
		if (!backEnd.validCity(city)) {

			System.out.println("The entered city is not valid.");
			listCities(scnr);
			city = enterCity(scnr);
		}

		return city;

	}

	/**
	 * Helper method that prompts the user for the number of stops they wish to make
	 * 
	 * @param scnr
	 * @return
	 */
	public static int enterNumStops(Scanner scnr) {
	  int num = 0;
	  while(true) {
	    System.out.println("How many destinations do you have? (not including starting point; must be 2 or 3): ");
	    scnr = new Scanner(System.in);
	    try {
	      num = scnr.nextInt();
	      if(num != 2 && num != 3) {
	        System.out.println("Please enter a valid number of stops");
	        continue;
	      }
	    }
	    catch(InputMismatchException e) {
	      System.out.println("Please enter a valid number of stops");
	      continue;
	    }
	    break;
	  }
	  return num;
	}

	/**
	 * Helper method that will let the user see the list of valid cities
	 * 
	 * @param scnr
	 */
	public static void listCities(Scanner scnr) {

		System.out.println("Type '3' to see a list of valid cities, or any key to continue.");
		char list = scnr.next().charAt(0);
		if (list == '3') {
			String[] cityList = backEnd.listCities();
			for (int i = 0; i < cityList.length; i++) {
				System.out.println(cityList[i]);
			}
			System.out.print("\nEnter a destination: \n");
		} else {
			System.out.print("\nEnter a destination: \n");
		}

		

	}

	/**
	 * The method takes a char parameter from the user and runs the corresponding
	 * command
	 * 
	 * @param input - the char entered by the user to select a command
	 */
	public static void processUserInput(char input) {
		Scanner scnr;
		ArrayList<String> destinations = new ArrayList<String>();

		switch (Character.toUpperCase(input)) {
		case 'R':
			if (route.length == 0) {
				scnr = new Scanner(System.in);

				state = enterState(scnr);

				try {
					createGraph(state);
				} catch (Exception e) {
					e.printStackTrace();
				}

				int numStops = enterNumStops(scnr);

				System.out.println("Enter your starting destination: ");
				listCities(scnr);
				String startDest = scnr.next();

				while (!backEnd.validCity(startDest)) {

					System.out.print("Please enter a valid city.\n");
					startDest = scnr.next();
				}

				destinations.add(startDest);

				for (int i = 1; i <= numStops; i++) {
					System.out.print("Enter a destination: \n");
					destinations.add(enterCity(scnr));
				}

				// Prints out the delivery route that was just created
				try {
					if (destinations.size() == 3) {
						route = backEnd.shortestPath(destinations.get(0), destinations.get(1), destinations.get(2));

					} else if (destinations.size() == 4) {
						route = backEnd.shortestPath(destinations.get(0), destinations.get(1), destinations.get(2),
								destinations.get(3));

					}
				} catch (NullPointerException e) {
					System.out.println("No route can be created with these destinations. Please try again.");
					route = new CityType[0];
					state = "";
					backEnd.clear();
					break;
				}
				System.out.println("The route has been stored. Enter 'S' to view it.");

			} else {
				System.out.print("There is already a created route. Enter 'C' to clear it.");
			}
			break;

		// Prints the current Delivery route to the console
		case 'S':

			if (route.length != 0) {
				System.out.print("\n**Delivery Route starting at  " + route[0].getCityName().toUpperCase() + ", "
						+ state.toUpperCase() + ".**\n");

				for (int i = 1; i < route.length - 1; i++) {
					System.out.println("The next stop will be " + route[i].getCityName() + ".");
				}
				System.out.println("The final stop will be " + route[route.length - 1].getCityName() + ".");

			} else {
				System.out.print("There is no current route to show.");
			}

			break;

		// Clears the currently stored delivery route
		case 'C':
			if (route.length != 0) {
				route = new CityType[0];
				state = "";
				backEnd.clear();
				System.out.print("The route has been cleared.");
			} else {
				System.out.print("There is no current route.");
			}
			break;

		// Prints a list of how many diners and truck stops are along the current route
		case 'D':
			if (route.length != 0) {
				System.out.print("\n**Diners and Trucks Stops**\n");
				for (int i = 1; i < route.length; i++) {
					System.out.println("\n" + route[i].getCityName() + ":" + "\n\tDiners: " + route[i].getNumDiners()
							+ "\n\tTruck Stops: " + route[i].getNumTruckStops());
				}

			} else {
				System.out.print("There is no current route.");
			}
			break;

		// Reprints the menu for the delivery route application
		case 'M':
			System.out.println(MENU);
			break;

		// The default case for when an invalid command is entered
		default:
			System.out.println("WARNING. Invalid command. Please enter M to refer to the menu.");
		}

	}

	/**
	 * This method runs the processUserInput method and will continue running until
	 * input from the user quits the application
	 */
	private static void driver() {
		Scanner scnr = new Scanner(System.in);

		System.out.print(MENU);
		System.out.print(PROMPT_COMMAND);
		char c = scnr.next().charAt(0);

		while (Character.toUpperCase(c) != 'Q') {
			processUserInput(c);
			System.out.println(PROMPT_COMMAND);
			c = scnr.next().charAt(0);
		}
		scnr.close();
	}

	/**
	 * This main method runs the driver method along with printing the welcome and
	 * goodbye messages.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(WELCOME);
		driver();
		System.out.println(GOODBYE);
	}
}
