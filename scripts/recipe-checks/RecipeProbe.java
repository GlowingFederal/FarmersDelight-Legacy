import com.sun.jdi.*;
import com.sun.jdi.connect.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;
import java.util.*;
public class RecipeProbe {
 static VirtualMachine vm;static ThreadReference thread;static ObjectReference loader;
 static List<Value> values(Value...v){return Arrays.asList(v);}
 static Method method(ReferenceType type,String name,int count){return type.methodsByName(name).stream().filter(m->m.argumentTypeNames().size()==count&&!m.isAbstract()&&(!name.equals("eval")||m.signature().equals("(Ljava/lang/String;)Ljava/lang/Object;"))).findFirst().orElseThrow(()->new IllegalStateException(type.name()+"."+name));}
 static Value call(ObjectReference object,String name,Value...args)throws Exception{return object.invokeMethod(thread,method(object.referenceType(),name,args.length),values(args),ObjectReference.INVOKE_SINGLE_THREADED);}
 static ClassType type(String name)throws Exception{if(!vm.classesByName(name).isEmpty())return (ClassType)vm.classesByName(name).get(0);ClassType c=(ClassType)vm.classesByName("java.lang.Class").get(0);ClassObjectReference loaded=(ClassObjectReference)c.invokeMethod(thread,method(c,"forName",3),values(vm.mirrorOf(name),vm.mirrorOf(true),name.startsWith("jdk.nashorn.")?stat("java.lang.ClassLoader","getSystemClassLoader"):loader),ObjectReference.INVOKE_SINGLE_THREADED);return (ClassType)loaded.reflectedType();}
 static ObjectReference make(String name)throws Exception{ClassType type=type(name);return type.newInstance(thread,type.methodsByName("<init>").stream().filter(m->m.signature().equals("()V")).findFirst().orElseThrow(),values(),ObjectReference.INVOKE_SINGLE_THREADED);}
 static Value stat(String name,String nameOfMethod,Value...args)throws Exception{ClassType type=type(name);return type.invokeMethod(thread,method(type,nameOfMethod,args.length),values(args),ObjectReference.INVOKE_SINGLE_THREADED);}
 static void probe(String owner,String scriptFile)throws Exception{
  loader=vm.classesByName(owner).get(0).classLoader();ObjectReference engine=(ObjectReference)call(make("jdk.nashorn.api.scripting.NashornScriptEngineFactory"),"getScriptEngine");String script=java.nio.file.Files.readString(java.nio.file.Path.of(scriptFile));
  try{Value result=call(engine,"eval",vm.mirrorOf(script));System.out.println(result instanceof StringReference s?s.value():result);}catch(InvocationException e){System.out.println("REMOTE "+call(e.exception(),"toString"));call(e.exception(),"printStackTrace");throw e;}
 }
 public static void main(String[]args)throws Exception{
  AttachingConnector connector=Bootstrap.virtualMachineManager().attachingConnectors().stream().filter(c->c.name().endsWith("SocketAttach")).findFirst().orElseThrow();Map<String,Connector.Argument> settings=connector.defaultArguments();settings.get("hostname").setValue("127.0.0.1");settings.get("port").setValue(args[1]);vm=connector.attach(settings);BreakpointRequest bp=null;
  try{boolean client=args.length>3&&args[3].equals("client");ClassType owner=(ClassType)vm.classesByName(client?"net.minecraft.client.Minecraft":"net.minecraft.server.MinecraftServer").get(0);bp=vm.eventRequestManager().createBreakpointRequest(method(owner,client?"func_71411_J":"func_71217_p",0).location());bp.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);bp.enable();long deadline=System.currentTimeMillis()+15000;
   while(System.currentTimeMillis()<deadline){EventSet events=vm.eventQueue().remove(1000);if(events==null)continue;try{for(Event event:events)if(event instanceof BreakpointEvent hit){thread=hit.thread();bp.disable();probe(args[2],args[0]);return;}}finally{events.resume();}}throw new IllegalStateException("No game-thread boundary within 15 seconds");
  }finally{try{if(bp!=null)vm.eventRequestManager().deleteEventRequest(bp);vm.dispose();}catch(VMDisconnectedException ignored){}}
 }
}
