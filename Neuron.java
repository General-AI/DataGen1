package Net2;

public class Neuron {
	int voltage; //
	final int THRESHOLD;
	final int POSTSYNAPTIC_VOLTAGE;
	final int VOLTAGE_EQUILIB_RATE; // rate at which voltage returns to 0. (From both positive or negative)
	int currSynapseStrength;
	final int MAX_SYNAPSE_STRENGTH;
	int potentialTime; //time it takes for potential to propagate (NOTE: IN THE FUTURE MAKE IT SO THIS CAN VARY - REPRESENTING MYELENATION.
	int sending; // if 0, potential is not currently being sent. If being sent, sending = potentialTime and decrements by 1 at each step until sending = 0
	//when sending hits 0, the potential arrives at postsynaptic neuron
	
	int currLearningVal;
	final int LEARNING_VAL;
	final int LEARNING_RATE;

	public Neuron(int threshold, int postsynapticVoltage, int voltage_equilib_rate, int maxSynapseStrength, int potentialTime, int learningVal, int learningRate){
		this.THRESHOLD = threshold;
		this.POSTSYNAPTIC_VOLTAGE = postsynapticVoltage;
		this.VOLTAGE_EQUILIB_RATE = voltage_equilib_rate;
		this.MAX_SYNAPSE_STRENGTH = maxSynapseStrength;
		this.currSynapseStrength = maxSynapseStrength;
		this.potentialTime = potentialTime;
		this.LEARNING_VAL = learningVal;
		this.LEARNING_RATE = learningRate;
		this.currLearningVal = this.LEARNING_VAL;
		voltage = 0;
		sending = 0;
	}
	
}
