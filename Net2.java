package Net2;

import Net2.Gen1;
import Net2.Neuron;

public class Net2 {
	Gen1 generator;
	private final int NUMBER_INPUT_NEURONS;
	private final int NUMBER_HIDDEN_NEURONS;
	Neuron[] inputNeuron;
	Neuron[] hiddenNeuron;
	int[][] inputAM; //adjacency matrix: input->hidden
	int[][] adjacencyMatrix; //adjacency matrix hidden->hidden
	int maxSynapseStrength; //Does not take into account changes in synapse "weights"
	private final int REUPTAKE_RATE;
	private final int THRESHOLD;
	private final int POSTSYNAPTIC_VOLTAGE; //voltage after neuron fires. //make 
	private final int VOLTAGE_EQUILIB_RATE;
	private final int LEARNING_VAL;
	private final int LEARNING_RATE; //rate at which new synapses are formed there is a 1 in LearningVal probability new synapse will form. Each time synapse activated, drops to 1 in learningVal - LearningRate
	int potentialTime; //time it takes for axon potential to be propagated
	int signalsPerCharGen; //number of times input neuron gets a signal per each character generated
	//Later step: include individual synapse weights?
		
	
	public Net2(int numberInputNeurons, int numberHiddenNeurons, int maxSynapseStrength, int reuptakeRate, int threshold, int postsynapticVoltage, int voltageEquilibRate, int learningVal, int learningRate, Gen1 generator, int potentialTime, int signalsPerCharGen){
		this.NUMBER_INPUT_NEURONS = numberInputNeurons;
		this.NUMBER_HIDDEN_NEURONS = numberHiddenNeurons;
		this.REUPTAKE_RATE = reuptakeRate;
		this.maxSynapseStrength = maxSynapseStrength;
		this.THRESHOLD = threshold;
		this.POSTSYNAPTIC_VOLTAGE = postsynapticVoltage;
		this.VOLTAGE_EQUILIB_RATE = voltageEquilibRate;
		this.LEARNING_VAL = learningVal;
		this.LEARNING_RATE = learningRate;
		this.generator = generator;
		this.potentialTime = potentialTime;
		this.signalsPerCharGen = signalsPerCharGen;
		
		this.inputNeuron = new Neuron[NUMBER_INPUT_NEURONS];
		this.hiddenNeuron = new Neuron[NUMBER_HIDDEN_NEURONS];
		this.adjacencyMatrix = new int[NUMBER_HIDDEN_NEURONS][NUMBER_HIDDEN_NEURONS];
		this.inputAM = new int[NUMBER_INPUT_NEURONS][NUMBER_HIDDEN_NEURONS];
		
		//create neurons
		for(int i = 0; i < NUMBER_INPUT_NEURONS; i++){
			this.inputNeuron[i] = new Neuron(this.THRESHOLD, this.POSTSYNAPTIC_VOLTAGE , this.VOLTAGE_EQUILIB_RATE, this.maxSynapseStrength, this.potentialTime, this.LEARNING_VAL, this.LEARNING_RATE);
		}
		for(int i = 0; i < NUMBER_HIDDEN_NEURONS; i++){
			this.hiddenNeuron[i] = new Neuron(this.THRESHOLD, this.POSTSYNAPTIC_VOLTAGE, this.VOLTAGE_EQUILIB_RATE, this.maxSynapseStrength, this.potentialTime, this.LEARNING_VAL, this.LEARNING_RATE);
		}
		
		//create synapses
		System.out.println("Connecting Inputs...");
		connect26Inputs();
		System.out.println("Connecting Hidden...");
		connectHidden();
		System.out.println("Network Initialized");
	
		
	}
	//1 step is defined as processing one character from generator signalsPerCharGen times.
	public void run(int nsteps){
		
		int numChars = 1000; //generator caps queue at around 1000 characters. Generates more if needed
		char currentChar = '$'; //current char (I only initialized it so the compiler would stop bugging out)
		
		int count = 0; //counts number of times a single character is provided as input up to signalsPerCharGen times. Then next character is generated and count resets.	
		int tick = 0; //Used to keep track of number of ticks
		while(tick < signalsPerCharGen*nsteps){			
			System.out.println("TICK " + tick);
			
			/* PART 1:
			 * add impulse from data
			 * aggregate inputs to neurons and add to voltage
			 * update "sending" (Decrement by 1)
			 * update "synapse strength" (increment by reuptake rate)
			 */
			if(generator.q.isEmpty()){
				generator.generate(numChars);
			}
			
			if(count == 0){	//removes next char from queue
				currentChar = generator.q.remove();
			}
			count = (count+1)%signalsPerCharGen;
		
			//to test:
			/*
			if(tick<= 20){
			currentChar = 'd';
			}
			else
				currentChar = '$';
			*/	
			//currentChar = 'd';
				
			
			int charNum = currentChar - 'a';		
			//increase voltage corresponding input neuron to 50 (causes potential to send each time);
			
			if(currentChar != '$'){
				inputNeuron[charNum].voltage += 50;	
			}

			//aggregate inputs to hidden neurons
			for(int i = 0; i < hiddenNeuron.length; i++){
				Neuron n = hiddenNeuron[i];
				
				//no potential = do nothing.
				
				//input to next neurons
				if(n.sending == 1){
					n.sending--;
					int[] neighbors = getNeighborIndices(i);
					for(int j = 0; j < neighbors.length; j++){
						if(neighbors[j] >= NUMBER_HIDDEN_NEURONS || neighbors[j] < 0){
							continue;
						}
						int s = adjacencyMatrix[i][neighbors[j]];
						if(s != 0){
							hiddenNeuron[neighbors[j]].voltage += (n.currSynapseStrength*s);
						}
					}
					n.currSynapseStrength = 0;
					learn(i, true);
				}
				if(n.sending > 1){
				//there is a potential that hasn't arrived yet. decrement sending.
				n.sending--;
				}
				
				//update currsynapsestrength by reuptake rate
				if(n.MAX_SYNAPSE_STRENGTH - n.currSynapseStrength <= this.REUPTAKE_RATE)
					n.currSynapseStrength = n.MAX_SYNAPSE_STRENGTH;
				else
					n.currSynapseStrength += this.REUPTAKE_RATE;
			}
			
			//aggregate inputs from input neurons
			for(int i = 0; i< inputNeuron.length; i++){
				Neuron n = inputNeuron[i];

				//no potential = do nothing
				
				//input to next neurons
				if(n.sending == 1){
					n.sending--;
					
					int c = (int) Math.cbrt(NUMBER_HIDDEN_NEURONS);
					for(int k = 0; k < c*c; k++){
						if(inputAM[i][k] != 0){
							hiddenNeuron[k].voltage += (n.currSynapseStrength*inputAM[i][k]);
							break;
						}
					}
					n.currSynapseStrength = 0;
					learn(i, false);
				}
				if(n.sending > 1){
					//there is a potential that hasn't yet arrived. decrement sending.
					n.sending--;
				}	
				
				//update currsynapsestrength by reuptake rate
				if(n.MAX_SYNAPSE_STRENGTH - n.currSynapseStrength <= this.REUPTAKE_RATE)
					n.currSynapseStrength = n.MAX_SYNAPSE_STRENGTH;
				else
					n.currSynapseStrength += this.REUPTAKE_RATE;
			}
			
			/*PART 2:
			 * check if neuron will fire (compare voltage to threshold).
			 * update voltages accordingly (if fired, set to postsynaptic voltage. else, decrement voltage accordingly)
			 * 
			 */
			System.out.print("H NEURONS: ");
			for(int i = 0; i<hiddenNeuron.length; i++){
				Neuron n = hiddenNeuron[i];
				//action potential cannot send when a potential is already sending
				if(n.sending != 0)
					continue;
				if(n.voltage >= n.THRESHOLD){
					System.out.print(i + ", "); //outputs neurons that fired on this tick
					n.voltage = n.POSTSYNAPTIC_VOLTAGE;
					n.sending = n.potentialTime;
				}
				else{
					n.voltage = (n.voltage*n.VOLTAGE_EQUILIB_RATE)/100;
				}
			}
			System.out.println();
			System.out.print("I NEURONS: ");
			for(int i = 0; i<inputNeuron.length; i++){
				Neuron n = inputNeuron[i];
				//action potential cannot send when a potential is already sending
				if(n.sending != 0)
					continue;
				if(n.voltage >= n.THRESHOLD){
					System.out.print(i + ", ");
					n.voltage = n.POSTSYNAPTIC_VOLTAGE;
					n.sending = n.potentialTime;
				}
				else{
					n.voltage = (n.voltage*n.VOLTAGE_EQUILIB_RATE)/100;
				}
			}
			System.out.println("");
			System.out.println("");
			//
			tick++;
			
			//currentChar = 'g';
			/*DEBUG
			
			Neuron a = inputNeuron['c' - 'a'];
			System.out.println("DEBUG");
			System.out.println("voltage: " + a.voltage);
			System.out.println("voltage: " + a.voltage);
			System.out.println("sending: " + a.sending);	
			*/
		}
	}
	
	//initialize input neurons to connect onto the cube.
	private void connect26Inputs(){
		int sideLength = (int) Math.cbrt(NUMBER_HIDDEN_NEURONS);
		int count = 0;
		for(int j = 0; j < 6; j++){
			for(int k =0; k < 6; k++){
				inputAM[count][sideLength*j + (sideLength/5)*k] = 1; //scatters input connections to the front 2-d face of the 3-d cube
				if(count == 25)
					return;
				count++;
			}
		}

	}
	
	//initialize hidden neurons to connect to a random set of their neighbors. Let's say we'll create 8 synapses.
	private void connectHidden(){
		int n = 8; //n is number of synapses to connect to.
		
		for(int i = 0; i < NUMBER_HIDDEN_NEURONS; i++){
			int[] neighbors = getNeighborIndices(i);
			
			for(int j = 0; j < n; j++){
				int r = (int) (Math.random()*neighbors.length);
				if(neighbors[r] >= NUMBER_HIDDEN_NEURONS || neighbors[r] < 0){
					j--;
					continue;
				}
				adjacencyMatrix[i][neighbors[r]]++;
			}
			
		}
	}
	
	//gets all 26 neighboring indices of a given neuron in cube
	private int[] getNeighborIndices(int index){
		int sideLength = (int) Math.cbrt(NUMBER_HIDDEN_NEURONS);
		int[] list = new int[26];
		
		int count = 0;
		for(int i = -1; i<=1; i++){
			for(int j = -1; j<=1; j++){
				for(int k = -1; k<=1; k++){
					if(i == 0 && j == 0 && k == 0)
						continue;
					list[count] = index + i*sideLength*sideLength + j*sideLength + k;
					count++;
				}
			}
		}
		return list;
		
	}

	//applies learning rule to neuron at index.
	private void learn(int index, Boolean hidden){
		if(hidden){
			Neuron n = hiddenNeuron[index];
			int num = (int) (Math.random()*n.currLearningVal);
			if(num == 0){
				//increment a random synapse that is not 0
				int[] neighbors = getNeighborIndices(index);
				int r = (int) (Math.random()*neighbors.length);
				int startingr = r; //this keeps track of starting r value so if there are no synapses from a neuron, the below while loop is not infinite
				while(true){
					if(neighbors[r] >= NUMBER_HIDDEN_NEURONS || neighbors[r] < 0){
						r = (r+1)%neighbors.length;
					}
					else{
						if(adjacencyMatrix[index][neighbors[r]] != 0){
							adjacencyMatrix[index][neighbors[r]]++;
							break;
						}
						r=(r+1)%neighbors.length;
					}
					if(startingr == r)
						break;
				}
				//Since new synapse was just formed, reset currLearningVal to LearningVal
				n.currLearningVal = n.LEARNING_VAL;
			}
			else{
				//adjusts currLearningVal by LearningRate making it more likely that a synapse is formed in subsequent iterations.
				if(n.currLearningVal - n.LEARNING_RATE <= 0)
					n.currLearningVal = 0;
				else
					n.currLearningVal -= n.LEARNING_RATE;
			}
		}
		else{
			//same thing just for input neurons **THIS ASSUMES EACH INPUT NEURON IS CONNECTED TO JUST 1 HIDDEN NEURON**
			Neuron n = inputNeuron[index];
			int num = (int) (Math.random()*n.currLearningVal);
			if(num == 0){
				int c = (int) Math.cbrt(NUMBER_HIDDEN_NEURONS);
				for(int i = 0; i < c*c; i++){
					if(inputAM[index][i] != 0){
						inputAM[index][i]++;
						break;
					}
				}
				//Since new synapse was just formed, reset currLearningVal to LearningVal
				n.currLearningVal = n.LEARNING_VAL;
			}
			else{
				//adjusts currLearningVal by LearningRate making it more likely that a synapse is formed in subsequent iterations.
				if(n.currLearningVal - n.LEARNING_RATE <= 0)
					n.currLearningVal = 0;
				else
					n.currLearningVal -= n.LEARNING_RATE;
			}
		}
	}
}
