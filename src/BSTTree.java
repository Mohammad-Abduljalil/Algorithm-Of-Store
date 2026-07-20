

public class BSTTree {
    public BSTTree() {
    }

    public Shipment Insert(Shipment root, Shipment shipment) {
        if (root == null) {
            return shipment;
        } else if (shipment.getShipmentId() < root.getShipmentId()) {
            root.left = Insert(root.left, shipment);
        } else if (shipment.getShipmentId() > root.getShipmentId()) {
            root.right = Insert(root.right, shipment);
        } else return null;
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
                Shipment temp = minValueShipment(root.right);
                root = temp;
                root.right = deleteShipment(root.right, temp.getShipmentId());
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