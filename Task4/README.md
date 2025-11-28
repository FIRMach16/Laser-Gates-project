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
    
