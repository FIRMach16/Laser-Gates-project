This is a bare bone plan that I will follow to complete this Task:

- Define essential and complementary functionalites influenced by the components, PCB and the enclosure
- Design the schematic 
- Verification and testing(simulated)
- Write the Bill of Materials following best practices
- Routing
- Print the PCBs (PCBway or other platforms (price and component availablity will matter the most))
- Testing the physical PCB
- Designing the enclosures
- Printing the enclosures
- Assembly of components and eclosures 
- Pray it works
---

To document my progress in this task I will use a journal-like format where I justify the features/design choices that I'm adding.

- 27/11/2025 :

    - For the main logic I have setteled on the the esp32-s2-SOLO2 module that is low-power, single core and have wifi only (No BLE).
    - For power i will chose a LiPo 3.7v for it's ability to be recharged and it's availability, and personnaly i belive it's good choice because it will give me a good reference for other future projects.
    - For power regulation i will use a LDO (AMS1117-3.3V) as it cheap and simple enough. **Note:** because my project will need 2 functioning boards at the same time i will not include a TP4056 module to reduce cost and also to reduce risk as i know that including a LiPo charging module can significantly increase the difficulty of designing a PCB (read skill issue) however I may include it in future designs (after the first prototype works).
    - I decided add audio to the start gate to simulate the "On your marks, set ,GO!" of a real race as this will allow an athlte to measure his run time + his reaction time which will give a time closer to the one of a competition.
    - I decided to do one PCB for both the start gate and the gate, however each will have it's own specific BOM as printing the same board twice is significantly cheaper than have 2 unique designs.
- 29/11/2025:

    - After more consideration, I belive that using a Bluetooth speaker for audio is a better solution than using a built-in speaker or even designing a built-in Audio Jack here are the pros and cons of this decision:
   **Pros:**
    1. I can add range to the total distance sprinted (Speaker <-20m-> StartGate <-+30m-> EndGate)
    2. The amplification is much better than what I could design with a 3.7v battery.
    3. Most speakers nowadays use Bluetooth to play audio.
      **Cons:**
    1. I need to buy, build or find a bluetooth Speaker , to test this I'm going to use my Pc speakers, You will find the program for this [here](../Task2/BluetoothAudio).
    2. Switching between Bluetooth (For Audio Commands) and Wifi (Which will command the EndGate to start the Timer) may introduce delay in timekeeping (I want the accumulated delay to be less than 10ms). A potential workaround is to send the introduced delay value alongside the signal to start the timer, and then include it in the calculations of the sprint time.
    - What All of this mean that I need to use the ESP32 familly instead of others because it's the only one that provide the BR (basic rate) bluetooth, so no ESP32-S2.
    - Hopefully I don't change my opinion again.
- 19/12/2025:
      - At the end I decided to use a wired in speaker instead of a bluetooth one because the bluetooth present an unacceptable delay (100ms) with no way of including it in the calculations.
      - As of today the first pcb version is complete.

    

<img width="1081" height="611" alt="Screenshot from 2025-12-19 17-19-24" src="https://github.com/user-attachments/assets/21b760d3-fffd-4007-93e8-bbe5a7339f11" />

**Features :**
- 2 layers PCB.
- An 3.3v LDO to regulate voltage.
- A MAX98357A amplifier to use a built-in speaker with the I2S protocol.
- An esp32-solo2 as the microcontroller of the chip.
