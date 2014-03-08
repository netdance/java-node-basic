
package net.netdance.example.node

import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class BasicNodeExample {
	
    static void main(String[] args) {
        println "in main"
            
        
        File semverjs = new File('./node_modules/semver/semver.js')
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");
        Invocable inv = (Invocable) engine;

        engine.eval('exports = {}')
        engine.eval(semverjs.text);
        engine.eval('semver = exports')
        def semver = engine.get('semver')
       
        println 'semver.clean("1.2.3") with eval'
        println engine.eval('semver.clean("1.2.3")')
        
        println 'semver.clean("1.2.3") with invoke'
        println inv.invokeMethod(semver,"clean", "1.2.3" );
        
        println "semver.gt('1.2.3', '9.8.7') with eval"
        println engine.eval("semver.gt('1.2.3', '9.8.7')")
        
        println "semver.lt('1.2.3', '4.5.6') with invoke"
        println inv.invokeMethod(semver,"lt",['1.2.3','4.5.6'] as Object[])

        println "semver.maxSatisfying(['1.2.3','1.3.0'],'~1') with eval"
        println engine.eval("semver.maxSatisfying(['1.2.3','1.3.0'],'~1')")

        try {
            println "semver.maxSatisfying(['1.2.3','1.3.0'],'~1') with invoke"
            println inv.invokeMethod(semver, 'maxSatisfying',
                [['1.2.3','1.3.0'] as Object[],'~1',true] as Object[])
        } catch (Exception e) {
            println "It failed with this message:"
            println e.message
        }
        
        println "semver.maxSatisfying(['1.2.3','1.3.0'],'~1') with double invoke"
        println inv.invokeMethod(semver, 'maxSatisfying',
            [inv.invokeMethod(engine.get('Java'),'from',['1.2.3','1.3.0']),'~1',true] as Object[])

        
        def shim =
'''
semver.maxSatisfyingHack = maxSatisfyingHack;
function maxSatisfyingHack(rversions, range, loose) {
  var versions = Java.from(rversions)
  return maxSatisfying(versions,range,loose);
}
'''           
        engine.eval(shim)
        
        println "semver.maxSatisfyingHack(['1.2.3','1.3.0'],'~1') with invoke and shim"
        println inv.invokeMethod(semver, 'maxSatisfyingHack',
            [['1.2.3','1.3.0'],'~1',true] as Object[])

    }
}

