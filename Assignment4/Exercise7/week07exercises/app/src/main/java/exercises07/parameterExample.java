package exercises07;
// Parameter passing example
// jst@itu.dk * 2022-09-22 Simple example to illustrate parameter passing

class parameterExample {
    public static void main(String[] args) {
        new parameterExample();
    }

    public parameterExample() {
        objectEx o = new objectEx();

        int i = 5;
        System.out.println("Before update:" + i);
        updateint(i);
        System.out.println("After update:" + i);

        o.setli(5);
        System.out.println("Before update:" + o.get());
        updateObject(o);
        System.out.println("After update:" + o.get());
    }

    public void updateObject(objectEx p) {
        p.setli(7);
    }

    public void updateint(int p) {
        p = 7;
    }

    class objectEx {
        private int li;

        public void setli(int v) {
            li = v;
        }

        public int get() {
            return li;
        }
    }
}