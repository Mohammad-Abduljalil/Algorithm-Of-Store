

public class BSTTree {
    Shipment root;

    public BSTTree() {
    }

    // ===== دوال غلاف (Wrapper) تتعامل تلقائياً مع حقل root =====
    public void insertShipment(Shipment shipment){
        root = Insert(root, shipment);
    }

    public void deleteShipmentByID(int ID){
        root = deleteShipment(root, ID);
    }

    public Shipment searchShipmentByID(int ID){
        return searchShipmentByID(root, ID);
    }
    // ===============================================================

    public Shipment Insert(Shipment root, Shipment shipment) {
        if (root == null) {
            return shipment;
        } else if (shipment.getShipmentId() < root.getShipmentId()) {
            root.left = Insert(root.left, shipment);
        } else if (shipment.getShipmentId() > root.getShipmentId()) {
            root.right = Insert(root.right, shipment);
        } else {
            // ID مكرر: لا تُضِف، لكن يجب إعادة root وليس null
            // (إعادة null كانت تُسقط الشجرة بأكملها من منظور المستدعي الأعلى)
            return root;
        }
        return root;
    }

    Shipment minValueShipment(Shipment ship) {
        Shipment Current = ship;
        while (Current.left != null) {
            Current = Current.left;
        }
        return Current;
    }

    Shipment deleteShipment(Shipment root, int ID) {
        if (root == null) {
            return root;
        }

        if (ID < root.getShipmentId()) {
            root.left = deleteShipment(root.left, ID);
        } else if (ID > root.getShipmentId()) {
            root.right = deleteShipment(root.right, ID);
        } else {

            if (root.left == null || root.right == null) {
                Shipment temp = (root.left!=null)?root.left:root.right;
                if (temp == null) {
                    temp = root;
                    root = null;
                } else {
                    root = temp;
                }
            } else {
                Shipment successor = minValueShipment(root.right);
                Shipment newRight = deleteShipment(root.right, successor.getShipmentId());
                successor.left = root.left;
                successor.right = newRight;
                root = successor;
            }
        }

        return root;
    }

    Shipment searchShipmentByID(Shipment root, int ID) {
        if (root == null) {
            return null;
        }
        if (root.getShipmentId() == ID) {
            return root;
        } else if (ID < root.getShipmentId()) {
            return searchShipmentByID(root.left, ID);
        } else if (ID > root.getShipmentId()) {
            return searchShipmentByID(root.right, ID);
        } else return null;
    }

}