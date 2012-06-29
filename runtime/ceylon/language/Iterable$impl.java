package ceylon.language;

import java.util.Arrays;
import java.util.Comparator;

import com.redhat.ceylon.compiler.java.Util;
import com.redhat.ceylon.compiler.java.metadata.Ignore;

@Ignore
public final class Iterable$impl<Element> {
    private final Iterable<Element> $this;
    public Iterable$impl(Iterable<Element> $this) {
        this.$this = $this;
    }
    public boolean getEmpty(){
        return Iterable$impl._getEmpty($this);
    }
    static <Element> boolean _getEmpty(Iterable<Element> $this){
        return $this.getIterator().next() instanceof Finished;
    }

    public Iterable<? extends Element> getSequence() {
        return Iterable$impl._getSequence($this);
    }
    static <Element> Iterable<? extends Element> _getSequence(Iterable<Element> $this) {
        final SequenceBuilder<Element> sb = new SequenceBuilder<Element>();
        sb.appendAll($this);
        return sb.getSequence();
    }

    public <Result> Iterable<Result> map(Callable<Result> collecting) {
        return new MapIterable<Element, Result>($this, collecting);
    }

    public Iterable<? extends Element> filter(Callable<? extends Boolean> selecting) {
        return new FilterIterable<Element>($this, selecting);
    }

    public <Result> Result fold(Result initial, Callable<? extends Result> accumulating) {
        return Iterable$impl._fold($this, initial, accumulating);
    }
    static <Result> Result _fold(Iterable<?> $this, Result initial, Callable<? extends Result> accum) {
        java.lang.Object elem;
        for (Iterator<?> iter = $this.getIterator(); !((elem = iter.next()) instanceof Finished); ) {
            initial = accum.$call(initial, elem);
        }
        return initial;
    }

    public Element find(Callable<? extends Boolean> selecting) {
        return Iterable$impl._find($this, selecting);
    }
    static <Element> Element _find(Iterable<? extends Element> $this, Callable<? extends Boolean> sel) {
        java.lang.Object elem;
        for (Iterator<? extends Element> iter = $this.getIterator(); !((elem = iter.next()) instanceof Finished);) {
            if (sel.$call(elem).booleanValue()) {
                return (Element)elem;
            }
        }
        return null;
    }

    public Element findLast(Callable<? extends Boolean> selecting) {
        return Iterable$impl._findLast($this, selecting);
    }
    static <Element> Element _findLast(Iterable<? extends Element> $this, Callable<? extends Boolean> sel) {
        java.lang.Object elem;
        java.lang.Object last = null;
        for (Iterator<? extends Element> iter = $this.getIterator(); !((elem = iter.next()) instanceof Finished);) {
            if (sel.$call(elem).booleanValue()) {
                last = elem;
            }
        }
        return (Element)last;
    }

    public Iterable<? extends Element> sorted(Callable<? extends Comparison> comparing) {
        return Iterable$impl._sorted($this, comparing);
    }
    static <Element> Iterable<? extends Element> _sorted(Iterable<? extends Element> $this, final Callable<? extends Comparison> comp) {
        if ($this.getEmpty()) {
            return (Iterable<? extends Element>) $empty.getEmpty();
        }
        Element[] array = Util.toArray($this, (Class<Element>) java.lang.Object.class);
        Arrays.sort(array, new Comparator<Element>() {
            public int compare(Element x, Element y) {
                Comparison result = comp.$call(x, y);
                if (result.largerThan()) return 1;
                if (result.smallerThan()) return -1;
                return 0;
            }
        });
        return new ArraySequence<Element>(array,0);
    }

    public boolean any(Callable<? extends Boolean> selecting) {
        return Iterable$impl._any($this, selecting);
    }
    static <Element> boolean _any(Iterable<? extends Element> $this, Callable<? extends Boolean> sel) {
        Iterator<? extends Element> iter = $this.getIterator();
        java.lang.Object elem;
        while (!((elem = iter.next()) instanceof Finished)) {
            if (sel.$call(elem).booleanValue()) {
                return true;
            }
        }
        return false;
    }

    public boolean every(Callable<? extends Boolean> selecting) {
        return Iterable$impl._every($this, selecting);
    }
    static <Element> boolean _every(Iterable<? extends Element> $this, Callable<? extends Boolean> sel) {
        Iterator<? extends Element> iter = $this.getIterator();
        java.lang.Object elem;
        while (!((elem = iter.next()) instanceof Finished)) {
            if (!sel.$call(elem).booleanValue()) {
                return false;
            }
        }
        return true;
    }
    public Iterable<? extends Element> skipping(long skip) {
        return Iterable$impl._skipping($this, skip);
    }
    static <Element> Iterable<? extends Element> _skipping(final Iterable<? extends Element> $this, final long skip) {
        return skip==0 ? $this : new AbstractIterable<Element>() {
            public final Iterator<? extends Element> getIterator() {
                final Iterator<? extends Element> iter = $this.getIterator();
                for (int i = 0; i < skip; i++) { iter.next(); }
                return iter;
			}
        };
    }
    public Iterable<? extends Element> taking(long take) {
        return Iterable$impl._taking($this, take);
    }
    static <Element> Iterable<? extends Element> _taking(final Iterable<? extends Element> $this, final long take) {
        if (take == 0) {
            return (Iterable)$empty.getEmpty();
        }
        else return new AbstractIterable<Element>() {
            @Override
            public final Iterator<? extends Element> getIterator() {
                return new Iterator<Element>() {
                    private final Iterator<? extends Element> iter = $this.getIterator();
                    private int i=0;
                    @Override
                    public java.lang.Object next() {
                        while (i++ < take) {
                            return iter.next();
                        }
                        return exhausted.getExhausted();
                    }
                };
            }
        };
    }
    public Iterable<? extends Element> by(long step) {
        return Iterable$impl._by($this, step);
    }
    static <Element> Iterable<? extends Element> _by(final Iterable<? extends Element> $this, final long step) {
        if (step == 1) {
            return $this;
        } else if (step <= 0) {
            throw new Exception(String.instance("step size must be greater than zero"));
        } else {
            return new AbstractIterable<Element>() {
                @Override
                public Iterator<? extends Element> getIterator() {
                    return new Iterator<Element>() {
                        private final Iterator<? extends Element> orig = $this.getIterator();
                        @Override
                        public java.lang.Object next() {
                            java.lang.Object e = orig.next();
                            for (int i = 1; i < step; i++) {
                                orig.next();
                            }
                            return e;
                        }
                    };
                }
            };
        }
    }

    public long count(Callable<? extends Boolean> f) {
        return Iterable$impl._count($this, f);
    }
    public static <Element> long _count(final Iterable<? extends Element> $this, Callable<? extends Boolean> f) {
        Iterator<? extends Element> iter = $this.getIterator();
        java.lang.Object elem;
        long c = 0;
        while (!((elem = iter.next()) instanceof Finished)) {
            if (f.$call(elem).booleanValue()) {
                c++;
            }
        }
        return c;
    }
}

class MapIterable<Element, Result> implements Iterable<Result> {
    final Iterable<Element> iterable;
    final Callable<? extends Result> sel;
    MapIterable(Iterable<Element> iterable, Callable<? extends Result> collecting) {
        this.iterable = iterable;
        sel = collecting;
    }

    class MapIterator implements Iterator<Result> {
        final Iterator<? extends Element> orig = iterable.getIterator();
        java.lang.Object elem;
        public java.lang.Object next() {
            elem = orig.next();
            if (!(elem instanceof Finished)) {
                return sel.$call(elem);
            }
            return elem;
        }
    }
    public Iterator<Result> getIterator() { return new MapIterator(); }
    public boolean getEmpty() { return getIterator().next() instanceof Finished; }

    @Override 
    @Ignore
    public Iterable<? extends Result> getSequence() { 
        return Iterable$impl._getSequence(this); 
    }
    @Override 
    @Ignore
    public Result find(Callable<? extends Boolean> f) { 
        return Iterable$impl._find(this, f); 
    }
    @Override @Ignore
    public Result findLast(Callable<? extends Boolean> f) {
        return Iterable$impl._findLast(this, f);
    }
    @Override 
    @Ignore
    public Iterable<? extends Result> sorted(Callable<? extends Comparison> f) { 
        return Iterable$impl._sorted(this, f); 
    }
    @Override 
    @Ignore
    public <R2> Iterable<R2> map(Callable<? extends R2> f) { 
        return new MapIterable<Result, R2>(this, f); 
    }
    @Override 
    @Ignore
    public Iterable<? extends Result> filter(Callable<? extends Boolean> f) { 
        return new FilterIterable<Result>(this, f); 
    }
    @Override 
    @Ignore
    public <R2> R2 fold(R2 ini, Callable<? extends R2> f) { 
        return Iterable$impl._fold(this, ini, f); 
    }
    @Override @Ignore
    public boolean any(Callable<? extends Boolean> f) {
        return Iterable$impl._any(this, f);
    }
    @Override @Ignore
    public boolean every(Callable<? extends Boolean> f) {
        return Iterable$impl._every(this, f);
    }
    @Override @Ignore
    public Iterable<? extends Result> skipping(long skip) {
        return Iterable$impl._skipping(this, skip);
    }
    @Override @Ignore
    public Iterable<? extends Result> taking(long take) {
        return Iterable$impl._taking(this, take);
    }
    @Override @Ignore
    public Iterable<? extends Result> by(long step) {
        return Iterable$impl._by(this, step);
    }
    @Override @Ignore
    public long count(Callable<? extends Boolean> f) {
        return Iterable$impl._count(this, f);
    }
}

class FilterIterable<Element> implements Iterable<Element> {
    final Iterable<Element> iterable;
    final Callable<? extends Boolean> f;
    FilterIterable(Iterable<Element> iterable, Callable<? extends Boolean> selecting) {
        this.iterable = iterable;
        f = selecting;
    }

    class FilterIterator implements Iterator<Element> {
        final Iterator<? extends Element> iter = iterable.getIterator();
        public java.lang.Object next() {
            java.lang.Object elem = iter.next();
            boolean flag = elem instanceof Finished ? true : f.$call(elem).booleanValue();
            while (!flag) {
                elem = iter.next();
                flag = elem instanceof Finished ? true : f.$call(elem).booleanValue();
            }
            return elem;
        }
    }
    public Iterator<Element> getIterator() { return new FilterIterator(); }
    public boolean getEmpty() { return getIterator().next() instanceof Finished; }
    @Override 
    @Ignore
    public Iterable<? extends Element> getSequence() { 
        return Iterable$impl._getSequence(this); 
    }
    @Override 
    @Ignore
    public Element find(Callable<? extends Boolean> f) { 
        return Iterable$impl._find(this, f); 
    }
    @Override @Ignore
    public Element findLast(Callable<? extends Boolean> f) {
        return Iterable$impl._findLast(this, f);
    }
    @Override 
    @Ignore
    public Iterable<? extends Element> sorted(Callable<? extends Comparison> f) { 
        return Iterable$impl._sorted(this, f); 
    }
    @Override 
    @Ignore
    public <Result> Iterable<Result> map(Callable<? extends Result> f) { 
        return new MapIterable<Element, Result>(this, f); 
    }
    @Override 
    @Ignore
    public Iterable<? extends Element> filter(Callable<? extends Boolean> f) { 
        return new FilterIterable<Element>(this, f); 
    }
    @Override 
    @Ignore
    public <Result> Result fold(Result ini, Callable<? extends Result> f) { 
        return Iterable$impl._fold(this, ini, f); 
    }
    @Override @Ignore
    public boolean any(Callable<? extends Boolean> f) {
        return Iterable$impl._any(this, f);
    }
    @Override @Ignore
    public boolean every(Callable<? extends Boolean> f) {
        return Iterable$impl._every(this, f);
    }
    @Override @Ignore
    public Iterable<? extends Element> skipping(long skip) {
        return Iterable$impl._skipping(this, skip);
    }
    @Override @Ignore
    public Iterable<? extends Element> taking(long take) {
        return Iterable$impl._taking(this, take);
    }
    @Override @Ignore
    public Iterable<? extends Element> by(long step) {
        return Iterable$impl._by(this, step);
    }
    @Override @Ignore
    public long count(Callable<? extends Boolean> f) {
        return Iterable$impl._count(this, f);
    }
}
