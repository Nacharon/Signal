# Signal Class Overview

The Signal class provides functionality similar to the **Godot Signal** system, allowing you to create and manage custom events in your project.

## How It Works

When you create a new signal, you define the **parameters** it will use. These parameters dictate the **types of methods** that can be connected to the signal.

### Key Concepts

- **Parameter Matching:**  
  When you define a signal with a specific parameter type (e.g., a **String parameter**), only methods with a matching parameter type (in this case, a **String**) can be connected. Methods with incompatible parameters (e.g., an **Integer**) cannot be connected.

- **Emitting Signals:**  
  When a signal is emitted, all methods connected to that signal are triggered. The **emit function** passes the defined parameters to the connected methods.

- **No Return Values:**  
  Connected methods do not return any values when called by the signal. The signal system is purely for triggering events, not for receiving feedback from the connected methods.

## Setup Instructions

To integrate the Signal class into your project:

1. **Download the Latest Version:**  
   Obtain the latest version of `signal.jar`.

2. **Add to Project:**  
   Place `signal.jar` in your project folder.

3. **Compile and Run Your Code:**  
   When compiling and running your project, include the `signal.jar` in your classpath by using the following command:

   ```bash
   javac -cp "filepath/signal.jar" YourMainClass.java
   java -cp "filepath/signal.jar" YourMainClass


## Documentation

You can find the full documentation [here](https://nacharon.github.io/Signal-Documentation/).
