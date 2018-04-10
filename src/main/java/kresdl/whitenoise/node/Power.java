package kresdl.whitenoise.node;

import java.awt.Component;
import java.io.Serializable;
import kresdl.whitenoise.App;
import kresdl.whitenoise.Main;
import kresdl.whitenoise.buffer.Stream;
import kresdl.whitenoise.controls.Field;
import kresdl.whitenoise.socket.In;
import kresdl.whitenoise.socket.Out;

@SuppressWarnings("serial")
public final class Power extends Modifier {

    public static class Info extends Node.Info implements Serializable {

        public final double v;

        Info(int x, int y, double v) {
            super(x, y);
            this.v = v;
        }
    }

    class Task extends Modifier.Task {

        Task(int i) {
            super(i);
        }

        @Override
        public void run() {
            update();
            Stream p1 = in[0].getStream(i);
            double qv = pow.get();
            for (int i = 0; i < n; i++) {
                dst[k++] = Math.pow(p1.get(), qv);
            }
        }
    }
    
    public static Power create(int x, int y, Main main, double val) {
        Power n = new Power(x, y, main, val);
        n.init();
        return n;
    }
    
    private final Field pow;

    private Power(int x, int y, Main main, double val) {
        super("Power", x, y, main);
        in = new In[] {
            new In(this)
        };
        
        pow = new Field(val, this);
        controls = new Component[] {
            pow
        };
        out = new Out(this);
 
        for (int i = 0; i < App.PARALLELISM; i++) {
            tasks.add(new Task(i));
        }
    }

    @Override
    public Info getInfo() {
        return new Info(x, y, pow.get());
    }
}
