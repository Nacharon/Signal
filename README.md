## Explain

The class functions similarly to the **Godot Signal**.

When you create a new signal, you specify **parameters** that define the **types of methods** that can be connected.

For example, if you create a signal with a **single String parameter**, you **cannot connect** a method that expects an **Integer parameter**.</p>
When the signal is **emitted**, all **connected methods are triggered** with the **parameters** provided by the **emit function**.</p>

The connected methods **cannot return any values**.

## How to set up

Download the last version and put the `signal.jar`

Put the `signal.jar` in your Project folder

When you compile and execute your code you add `-cp "filepath/signal.jar"`
