package signal;

import signal.exception.InvalidConnectionException;
import signal.exception.InvalidMethodNameException;
import signal.exception.InvalidMethodParametreException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * <p>This class functions similarly to a Godot Signal.</p>
 * 
 * <p>When creating a new signal, you specify parameters that define 
 * the types of methods that can be connected. For example, if you create 
 * a signal with a single <code>String</code> parameter, you cannot connect 
 * a method that expects an <code>Integer</code> parameter.</p>
 * 
 * <p>When the signal is emitted, all connected methods are triggered 
 * with the parameters provided by the emit function.</p>
 * 
 * <p>Note that connected methods cannot return any values.</p>
 * 
 * @author Nacharon
 * @version 1.1
 * @since 1.0
 */
public class Signal
{
    private ArrayList<HashMap<String,Object>> connected_methods = new ArrayList<>();

    private final Class<?>[] parameter_types;

    /**
     * Constructs a Signal with specified parameter types for methods that will be connected to it.
     * 
     * @param parameters the types of parameters that connected methods should accept; each can be any class type, 
     *                   but primitive types are not allowed. To specify a class type, use <code>YourClass.class</code>.
     * 
     * @since 1.0
     */
    public Signal(Class<?>... parameters)
    {
        this.parameter_types = parameters;
    }


    /**
     * Connects a method of a specific instance object to the Signal. The same method of an object 
     * cannot be connected more than once.
     * 
     * @param object_instance the instance of the object to connect
     * @param method_name the name of the method to connect
     * 
     * @throws IllegalAccessException if the method is not public
     * @throws InvalidConnectionException if the method of the object is already connected; you can check this with <code>isConnected</code>
     * @throws InvalidMethodNameException if the method does not exist in the object's class
     * @throws InvalidMethodParameterException if the method parameters do not match those defined by the Signal
     * 
     * @since 1.0
     */
    public void connect(Object object_instance, String method_name) throws IllegalAccessException, InvalidConnectionException, InvalidMethodNameException, InvalidMethodParametreException
    {   
        Class<?> current_class = object_instance.getClass();

        if (this.isConnected(object_instance, method_name))
            throw new InvalidConnectionException("The method " + method_name + " in class " + current_class.getName() + " is already connected");

        try
        {
            Method method = current_class.getMethod(method_name, this.parameter_types);
            this.addConnection(object_instance, method);
        }
        catch(NoSuchMethodException error)
        {
            boolean method_name_found = false;
            boolean method_acces = false;
            for (Method each_method : current_class.getDeclaredMethods())
                if (each_method.getName().equals(method_name))
                {
                    method_name_found = true;
                    if (each_method.canAccess(object_instance))
                        method_acces = true;
                }
            
            if (!method_name_found)
                throw new InvalidMethodNameException("The method " + method_name + " doesn't exist in class " + current_class.getName());

            else if (!method_acces)
                throw new IllegalAccessException("You don't have acces to use the method " + method_name + " in class " + current_class.getName());
            
            else
                throw new InvalidMethodParametreException("The method " + method_name + " in class " + current_class.getName() + " doesn't have the required parameters : " + this.getParametersString(this.parameter_types));
        }
    }


    /**
     * Disconnects the method of a specific instance object from the Signal.
     * 
     * @param object_instance the instance of the object to disconnect
     * @param method_name the name of the method to disconnect
     * 
     * @throws InvalidConnectionException if the method of the instance object is not connected
     * 
     * @since 1.0
     */
    public void disconnect(Object object_instance, String method_name) throws InvalidConnectionException
    {
        if (!this.isConnected(object_instance, method_name))
            throw new InvalidConnectionException("The method " + method_name + " in class " + object_instance.getClass().getName() + " is not connected");
        
        boolean is_disconnect = false;
        int i = 0;

        while (i < this.connected_methods.size() && !is_disconnect)
        {
            if (this.equals(object_instance, method_name, this.connected_methods.get(i)))
            {
                this.connected_methods.remove(i);
                is_disconnect = true;
            }

            i++;
        }
    }


    /**
     * Disconnects all methods from the Signal.
     * 
     * @since 1.0
     */
    public void disconnectAll()
    {
        this.connected_methods = new ArrayList<>();
    }


    /**
     * Checks if the specified method of the given instance object is connected to the Signal.
     * 
     * @param object_instance the instance of the object
     * @param method_name the name of the method
     * 
     * @return <code>true</code> if the method is connected; <code>false</code> otherwise
     * 
     * @since 1.0
     */
    public boolean isConnected(Object object_instance, String method_name)
    {
        for (HashMap<String,Object> dict : this.connected_methods)
            if (this.equals(object_instance, method_name, dict))
                return true;
        
        return false;
    }

    /**
     * Returns an <code>ArrayList</code> of <code>HashMap</code> objects, each containing two entries with keys 
     * <code>"Object"</code> and <code>"Method"</code>. The <code>"Object"</code> entry represents the connected object, 
     * and the <code>"Method"</code> entry represents the connected method.
     * 
     * @return an <code>ArrayList</code> of <code>HashMap</code> objects, where each <code>HashMap</code> contains 
     *         an <code>"Object"</code> and a <code>"Method"</code>
     * 
     * @since 1.1
     */
    public ArrayList<HashMap<String,Object>> getConnections()
    {
        return this.connected_methods;
    }


    /**
     * Emits the Signal and triggers all connected methods.
     * 
     * @param parameters the parameters that will be passed to all connected methods
     * 
     * @throws InvalidMethodParameterException if the given parameters do not match those defined by the Signal
     * @throws IllegalAccessException if a connected method is not public
     * 
     * @since 1.0
     */
    public void emit(Object... parameters) throws InvalidMethodParametreException, IllegalAccessException
    {
        for (HashMap<String,Object> dict : this.connected_methods)
        {
            try
            {
                if (dict.get("Method") instanceof Method method)
                    method.invoke(dict.get("Object"), parameters);
            }
            catch(InvocationTargetException error)
            {
                throw new InvalidMethodParametreException("The given parameters " + this.getParametersString(parameters) + " not match with Signal parametters in class " + this.getParametersString(this.parameter_types));
            }
        }
    }


    private void addConnection(Object object_instance, Method method)
    {
        HashMap<String, Object> dict = new HashMap<>();
        dict.put("Object",object_instance);
        dict.put("Method",method);
        this.connected_methods.add(dict);
    }


    private boolean equals(Object object_instance, String method_name, HashMap<String,Object> instance)
    {
        if (instance.get("Method") instanceof Method method) 
            return object_instance == instance.get("Object") && method_name.equals(method.getName());
        
        return false;
    }
    

    private String getParametersString(Class<?>[] parameter)
    {
        String parameter_string = "[";

        for (int i = 0; i < parameter.length; i++)
        {
            parameter_string += parameter[i].getName();
            if (i < parameter.length - 1)
                parameter_string += ", ";
        } 
        parameter_string += "]";

        return parameter_string;
    }

    private String getParametersString(Object[] parameter)
    {
        String parameter_string = "[";

        for (int i = 0; i < parameter.length; i++)
        {
            parameter_string += parameter[i].getClass().getName();
            if (i < parameter.length - 1)
                parameter_string += ", ";
        } 
        parameter_string += "]";

        return parameter_string;
    }
}




