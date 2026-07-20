import java.util.ArrayList;

public class HeapPriority {

    ArrayList<Order> arrayList;

    public HeapPriority(){
        arrayList =new ArrayList<>();
    }

    void AddOrder(Order order){
        arrayList.add(order);
        buildMaxHeapify();
    }

    void buildMaxHeapify(){
        for (int i = arrayList.size()/2-1; i >=0 ; i--) {
            maxHeapify(i);
        }
    }

    void maxHeapify(int i){
        int parent=i;
        int Left=2*i+1;
        int Right=2*i+2;

        if (Left < arrayList.size() && arrayList.get(Left).getPriority() > arrayList.get(parent).getPriority()){
            parent=Left;
        }
        if (Right < arrayList.size() && arrayList.get(Right).getPriority() > arrayList.get(parent).getPriority()){
            parent=Right;
        }
        if (parent!=i){
            Order swap= arrayList.get(parent);
            arrayList.set(parent, arrayList.get(i));
            arrayList.set(i,swap);
            maxHeapify(parent);
        }
    }

    Order DeleteRoot() {
        if (arrayList.isEmpty()){
            System.out.println(" There isn't any Order more! ");
            return null;
        }
        else if (arrayList.size()==1) {
            return arrayList.remove(0);
        }
        else {
            Order swap= arrayList.get(0);
            arrayList.set(0, arrayList.get(arrayList.size()-1));
            arrayList.remove(arrayList.size()-1);
            buildMaxHeapify();
            return swap;
        }

    }

    void printShipmentWithPriority(){
        for (int i = 0; i < arrayList.size(); i++) {
            System.out.print(arrayList.get(i)+" ");
        }
        System.out.println();
    }

}


