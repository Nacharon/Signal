package signal;

import signal.exception.InvalidConnectionException;
import signal.exception.InvalidMethodNameException;
import signal.exception.InvalidMethodParametreException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;

/**
 * <p>The class functions similarly to the Godot Signal.</p>
 * 
 * <p>When you create a new signal, you specify parameters that define 
 * the types of methods that can be connected.
 * <br>For example, if you create a signal with a single String parameter, 
 * you cannot connect a method that expects an Integer parameter.</p>
 * 
 * <p>When the signal is emitted, all connected methods are triggered 
 * with the parameters provided by the emit function.</p>
 * 
 * <p>The connected methods cannot return any values.</p>
 * 
 * @author Nacharon
 * @version 1.0
 * @since 1.0
 */
public class Signal
{
    private ArrayList<TupleObjectMethod> connected_methods = new ArrayList<>();

    private final Class<?>[] parameter_types;

    /**
     * The constructor defines the parameter types of the methods that will be connected to the Signal.
     * 
     * @param parameters can be of any class type, but you cannot use primitive types. To specify a class type, you can use "YourClass.class".
     * 
     * @since 1.0
     */
    public Signal(Class<?>... parameters)
    {
        this.parameter_types = parameters;
    }


    /**
     * Connect the method of a specific instance object to the Signal. You cannot connect the same method of an object multiple times.
     * 
     * @param object_instance The instance of the object to connect.
     * @param method_name The name of the method to connect.
     * 
     * @exception IllegalAccessException Throws if the method is not public.
     * @exception InvalidConnectionException Throws if the method of the object is already connected. You can check this with "isConnected".
     * @exception InvalidMethodNameException Throws if the method does not exist in the object's class.
     * @exception InvalidMethodParametreException Throws if the method does not match the parameters defined by the Signal.
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
            connected_methods.add(new TupleObjectMethod(object_instance, method));
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
                throw new InvalidMethodParametreException("The method " + method_name + " in class " + current_class.getName() + " doesn't have the required parameters : " + get_parameters_string());
        }
    }


    /**
     * Disconnect the method of a specific instance object from the Signal.
     * 
     * @param object_instance The instance of the object you want to disconnect.
     * @param method_name The name of the method to be disconnected.
     * 
     * @exception InvalidConnectionException Throws if the method of the instance object is not connected.
     * 
     * @since 1.0
     */
    public void disconnect(Object object_instance, String method_name) throws InvalidConnectionException
    {
        if (!this.isConnected(object_instance, method_name))
            throw new InvalidConnectionException("The method " + method_name + " in class " + object_instance.getClass().getName() + " is not connected");
        
        TupleObjectMethod temp = null;
        int i = 0;
        while (i < this.connected_methods.size() && temp == null)
        {
            if (this.connected_methods.get(i).equals(object_instance, method_name))
                temp = this.connected_methods.remove(i);
            i++;
        }
    }


    /**
     * Disconnect all methods from the Signal.
     * 
     * @since 1.0
     */
    public void disconnectAll()
    {
        this.connected_methods = new ArrayList<>();
    }


    /**
     * Returns true if the specified method is connected.
     * 
     * @param object_instance The instance of the object.
     * @param method_name The name of the method.
     * 
     * @return true if the method is connected, false otherwise.
     * 
     * @since 1.0
     */
    public boolean isConnected(Object object_instance, String method_name)
    {
        for (TupleObjectMethod method : this.connected_methods)
            if (method.equals(object_instance, method_name))
                return true;
        
        return false;
    }


    /**
     * Emit the Signal and trigger all connected methods.
     * 
     * @param parameters The parameters that will be passed to all connected methods.
     * 
     * @exception InvalidMethodParametreException Throws if the given parameters do not match those defined by the Signal.
     * @exception IllegalAccessException Throws if a connected method is not public.
     * 
     * @since 1.0
     */
    public void emit(Object... parameters) throws InvalidMethodParametreException, IllegalAccessException
    {
        for (TupleObjectMethod tuple : this.connected_methods)
        {
            try
            {
                tuple.method.invoke(tuple.object, parameters);
            }
            catch(InvocationTargetException error)
            {
                //add exception message
                throw new InvalidMethodParametreException("");
            }
        }
    }


    private String get_parameters_string()
    {
        String parameter_string = "[";

        for (int i = 0; i < this.parameter_types.length; i++)
        {
            parameter_string += this.parameter_types[i].getName();
            if (i < this.parameter_types.length - 1)
                parameter_string += ", ";
        } 
        parameter_string += "]";

        return parameter_string;
    }
    
    private class TupleObjectMethod
    {
        public final Method method;
        public final Object object;

        public TupleObjectMethod(Object object, Method method)
        {
            this.object = object;
            this.method = method;
        }

        public boolean equals(Object object, String method_name)
        {
            return this.method.getName().equals(method_name) && this.object == object;
        }
    }
}




