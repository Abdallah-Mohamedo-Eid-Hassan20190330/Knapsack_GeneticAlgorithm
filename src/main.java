import javax.print.DocFlavor;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.spec.RSAOtherPrimeInfo;
import java.sql.Array;
import java.util.*;

public class main {

    static final int POPULATION_SIZE = 120 ;
    static final double Pc = 0.6 ;
    static final double Pm = 0.01 ;
    static final double NUM_GEN = 1000 ;


    static class Pair
    {
        int weight ;
        int value ;
    }

    static class pop_individuals implements Comparable<pop_individuals>
    {
        public String binary ;
        public int fitness ;
        public int weight ;
        public int roulette_start ;
        public int roulette_end ;
        public pop_individuals( String binary , int fitness , int weight)
        {
            this.binary = binary ;
            this.fitness = fitness ;
            this.weight = weight ;
        }

        @Override
        public int compareTo(pop_individuals o) {

                return this.fitness - o.get_fitness();

        }

        private int get_fitness() {
            return fitness;
        }
    }

    static class offsprings
    {
        public String binary ;
        public int fitness ;
        public int weight ;
        public int roulette_start ;
        public int roulette_end ;
        public offsprings( String binary , int fitness , int weight)
        {
            this.binary = binary ;
            this.fitness = fitness ;
            this.weight = weight ;
        }
    }

    public static String toBinary(int n, int length)
    {
        StringBuilder binary = new StringBuilder();
        for (long i = (1L << length - 1); i > 0; i = i / 2) {
            binary.append((n & i) != 0 ? "1" : "0");
        }
        return binary.toString();
    }

    public static String Flip (String gene)
    {
        if (gene.equals("0"))
        {
            return "1" ;
        }
        return "0";
    }

    public static boolean evaluate_weight (String chromosome , Pair [] pair , int knapsack_size)
    {
        int weight = 0 ;
        for (int i = 0 ; i < chromosome.length() ; i ++ )
        {
            weight += Integer.parseInt(String.valueOf(chromosome.charAt(i))) * pair[i].weight ;
        }
        //System.out.println(weight);
        return weight <= knapsack_size ;
    }

    public static int get_weight (String chromosome , Pair [] pair , int knapsack_size)
    {
        int weight = 0 ;
        String [] array = chromosome.split("") ;
        for (int i = 0 ; i < array.length ; i ++ )
        {
            weight += Integer.parseInt(array[i]) * pair[i].weight ;
        }
        //System.out.println(weight);
        return weight ;
    }

    public static int evaluate_fitness (String binary , Pair [] pair)
    {
        int value = 0 ;
        String []array = binary.split("");
        for (int i = 0 ; i < array.length ; i ++ )
        {
            value += Integer.parseInt(array[i]) * pair[i].value ;
        }
        //System.out.println(value);
        return value ;
    }

    public static String crossover(String first_parent , String second_parent)
    {
        Random rand = new Random() ;
        String [] hybrid = new String[4] ;
        int cross_index = 1 +  rand.nextInt(first_parent.length() -1 ) ;
        //System.out.println("this is the cross over point : " + cross_index);
        hybrid[0] = first_parent.substring(0 , cross_index) ;
        hybrid[1] = first_parent.substring(cross_index) ;
        hybrid[2] = second_parent.substring(0 , cross_index) ;
        hybrid[3] = second_parent.substring(cross_index);
        String first_offspring = hybrid[0] + hybrid[3] ;
        String second_offspring = hybrid[2] + hybrid[1] ;


        return first_offspring + " " + second_offspring ;
    }

    public static String mutation(String offspring , Pair [] pair , int knapsack_size) {
        boolean check_weight = true ;
        String chromosome = "";
        Random rand = new Random();
        while(check_weight) {

            String[] genes = offspring.split("");

            for (int i = 0; i < genes.length; i++) {
                double do_mutation = rand.nextDouble();
                if (do_mutation <= Pm) {
                    genes[i] = Flip(genes[i]);
                }
            }
            for (int i = 0; i < genes.length; i++) {
                chromosome += genes[i];
            }
            if (evaluate_weight(chromosome , pair , knapsack_size))
            {
                check_weight = false ;

            }
            else
            {
                chromosome = "" ;
            }
        }
        return  chromosome ;
    }


// main function
    public static void main(String args [] ) throws FileNotFoundException {

        // denoting the file to be read :
        File file = new File("E:/university/genetic_algorithms_assign/src/samples.txt");
        Scanner scan = new Scanner(file);

        ArrayList <String> items = new ArrayList<>();
        ArrayList <String> selected_parents = new ArrayList<>();
        ArrayList <String> all_offsprings = new ArrayList<>();
        ArrayList <String> after_mutation = new ArrayList<>() ;
        //ArrayList <String> pop_individuals = new ArrayList<>() ;
        int test_num = Integer.parseInt(scan.nextLine());
        Random rand = new Random() ;
        // important : it will break at this point when testing many test_num:
        // because , there will be no two new lines at the end of the file ;
        scan.nextLine();
        scan.nextLine();

        // looping through each test :
        for (int j = 0 ; j < test_num ; j ++ ) {


            int knapsack_size = Integer.parseInt(scan.nextLine());
           // System.out.println("this is the size of the sack!! : " + knapsack_size);
            int num_items = Integer.parseInt(scan.nextLine());
            //System.out.println(num_items);
            //System.out.println("this is the number of items : " + num_items);
            Pair[] pair = new Pair[num_items];
            pop_individuals[] individual = new pop_individuals[POPULATION_SIZE];
            offsprings[] next_generation = new offsprings[POPULATION_SIZE];

            // getting item after item , and fill the pairs :
            for (int i = 0; i < num_items; i++) {
                pair[i] = new Pair();
                items.add(scan.nextLine());
                //System.out.println(items.get(i));
                String temp[] = items.get(i).split(" ");
                pair[i].weight = Integer.parseInt(temp[0]);
                pair[i].value = Integer.parseInt(temp[1]);
            }
            // generating random number from 0 to num_items according to population size :
            // Intialization step :
            double tmp_max = Math.pow(2, num_items);
            int max = (int) tmp_max;
           // System.out.println("this is the max number allowed : " + max);
            // tomorro ISA  generate random numbers from 0 to max , then search for
            // representing them as binary numbers !!
            int iter = 0;
            while (iter < POPULATION_SIZE) {
                int x = rand.nextInt(max);
              //  System.out.println(x);
                String binary = toBinary(x, num_items);
              //  System.out.println(binary);
                if (evaluate_weight(binary, pair, knapsack_size)) {
                    //pop_individuals.add(binary) ;
                    int fitness = evaluate_fitness(binary, pair);
                    int weight = get_weight(binary, pair, knapsack_size);
                    individual[iter] = new pop_individuals(binary, fitness, weight);
                    iter++;
                } else {
                    continue;
                }

            }
          //  System.out.println(individual.length);
          //  System.out.println("form here is the targeted test");
            // selection process :
            int total_fitness = 0;
            for (int i = 0; i < individual.length; i++) {
                total_fitness += individual[i].fitness;
            }
            int start = 0;
            int end = 0;
            for (int i = 0; i < individual.length; i++) {
                individual[i].roulette_start = start;
                individual[i].roulette_end = start + individual[i].fitness;
                start = individual[i].roulette_end;
            }

            // selection , crossover and mutation :
            for (int num_gen = 0; num_gen < NUM_GEN; num_gen++) {
               // System.out.println("this is the " + num_gen + " iteration , so trace it !!!!!!!!!!!!!!");
                for (int pop = 0; pop < POPULATION_SIZE / 2; pop++)
                    for (int i = 0; i < 2; i++) // loop with ofsprings number : and increment 2 instead of 1
                    {
                        int choice = rand.nextInt(total_fitness);
                        int selected_individual;
                        // loop over all the individuals :
                        for (int index = 0; index < individual.length; index++) {
                            if (choice >= individual[index].roulette_start && choice < individual[index].roulette_end) {
                                selected_individual = index;
                                selected_parents.add(individual[selected_individual].binary);
                                //System.out.println("this the one selected " + individual[selected_individual].binary );
                            }
                            // make array to store the parent , then the next one , then get its binary
                            // inorder to enter it in the crossOver function
                        }
                        if (i == 1) {
                            double do_crossover = rand.nextDouble();
                            if (do_crossover <= Pc) {
                                String offspring_str = crossover(selected_parents.get(0), selected_parents.get(1));
                                String[] offsprings = new String[2];
                                offsprings = offspring_str.split(" ");
                                if (evaluate_weight(offsprings[0], pair, knapsack_size) && evaluate_weight(offsprings[1], pair, knapsack_size)) {
                              //      System.out.println("the first offspring : " + offsprings[0]);
                               //     System.out.println("the second offspring : " + offsprings[1]);
                                    all_offsprings.add(offsprings[0]);
                                    all_offsprings.add(offsprings[1]);
                                    selected_parents.clear();


                                } else {
                                    selected_parents.clear();
                                    i = -1;
                                    continue;
                                }
                            } else {
                             //   System.out.println("same as parent here ");
                                all_offsprings.add(selected_parents.get(0));
                                all_offsprings.add(selected_parents.get(1));
                                selected_parents.clear();
                            }

                        }


                    }

                // mutation goes here :
                // after selecting all the offsprings , let's do some mutation :
                for (int i = 0; i < all_offsprings.size(); i++) {
                    after_mutation.add(mutation(all_offsprings.get(i), pair, knapsack_size));
                }

                for (int i = 0; i < after_mutation.size(); i++) {
                    int fitness = evaluate_fitness(after_mutation.get(i), pair);
                    int weight = get_weight(after_mutation.get(i), pair, knapsack_size);

                    next_generation[i] = new offsprings(after_mutation.get(i), fitness, weight);


                }
                // copy the new generation to the parents :
                for (int i = 0; i < POPULATION_SIZE; i++) {
                    individual[i].binary = next_generation[i].binary;
                    individual[i].fitness = next_generation[i].fitness;
                    individual[i].weight = next_generation[i].weight;

                }

                selected_parents.clear();
                all_offsprings.clear();
                after_mutation.clear();
                items.clear();

            }

//            // printing the parents :
//            for (int i = 0 ; i < selected_parents.size() ; i ++ )
//            {
//                System.out.println("this is the selected parent : " + selected_parents.get(i));
//            }
//            // printing the offsprings :
//            for (int i = 0 ; i < all_offsprings.size() ; i ++)
//            {
//                System.out.println(all_offsprings.get(i));
//
//            }
//            System.out.println("///////////////////////////////////////////////");
//            for (int i = 0; i < POPULATION_SIZE; i++)
//            {
//                System.out.println(individual[i].binary);
//            }
            //////// after sorting according to fitness :
            Arrays.sort(individual);
//            System.out.println("after sorting");
//            for (int i = 0 ; i < POPULATION_SIZE ; i++)
//            {
//                System.out.println(individual[i].binary);
//            }
//            System.out.println("getting values ");
//            for (int i = 0 ; i < POPULATION_SIZE ; i++)
//            {
//                System.out.println(individual[i].get_fitness());
//            }
//
//            System.out.println("the optimal value !!!!");
            int f = evaluate_fitness(individual[individual.length-1].binary , pair) ;
            System.out.println(f + "      "+individual[individual.length-1].binary );

            if(scan.hasNextLine())
                scan.nextLine();
            if(scan.hasNextLine())
                scan.nextLine();



        }

    }
}
