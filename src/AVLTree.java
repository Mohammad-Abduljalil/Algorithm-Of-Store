

public class AVLTree {
    Product root;
    public AVLTree(){}

    public void insertProduct(Product product){
        root = Insert(root, product);
    }

    public void deleteProductByID(int ID){
        root = deleteProduct(root, ID);
    }

    public Product searchProductByID(int ID){
        return searchProductByID(root, ID);
    }

    int hight(Product prod) {
        if(prod==null){
            return 0;
        }
        return prod.getHight();
    }

    int max(int a,int b){
        return Math.max(a, b);
    }

    Product RightRotate(Product prod){
        Product x1=prod.left;
        Product x2=x1.right;

        x1.right=prod;
        prod.left=x2;

        prod.setHight(max(hight(prod.left),hight(prod.right))+1);
        x1.setHight(max(hight(x1.left),hight(x1.right))+1);
        return x1;
    }

    Product LeftRotate(Product prod){
        Product x1=prod.right;
        Product x2=x1.left;

        x1.left=prod;
        prod.right=x2;

        prod.setHight(max(hight(prod.left),hight(prod.right))+1);
        x1.setHight(max(hight(x1.left),hight(x1.right))+1);

        return x1;
    }

    int getBalance(Product prod) {
        if (prod==null)
            return 0;
        return hight(prod.left)-hight(prod.right);
    }

    Product Insert(Product prod,Product product){
        if (prod==null){
            System.out.println(" main.java.Product has Added ");
            return product;}

        if (product.getID()< prod.getID())
        {
            prod.left=Insert(prod.left,product);
        }
        else if (product.getID()> prod.getID())
        {
            prod.right=Insert(prod.right,product);
        }
        else
        {
            return prod;
        }
        prod.setHight(max(hight(prod.left),hight(prod.right))+1);

        int Balance=getBalance(prod);

        if (Balance >1 && product.getID()<prod.left.getID()){
            return RightRotate(prod);
        }

        if (Balance<-1 && product.getID()>prod.right.getID()){
            return LeftRotate(prod);
        }

        if (Balance >1 && product.getID() > prod.left.getID()){
            prod.left=LeftRotate(prod.left);
            return RightRotate(prod);
        }

        if (Balance <-1 && product.getID()<prod.right.getID()){
            prod.right=RightRotate(prod.right);
            return LeftRotate(prod);
        }
        return prod;
    }

    Product minValueProduct(Product prod){
        Product Current=prod;
        while (Current.left!=null){
            Current=Current.left;
        }
        return Current;
    }

    Product deleteProduct(Product root,int ID){
        if (root==null){
            return root;
        }

        if (ID< root.getID()){
            root.left=deleteProduct(root.left,ID);
        }
        else if (ID> root.getID()){
            root.right=deleteProduct(root.right,ID);
        }
        else{

            if (root.left==null ||root.right==null){
                Product temp=null;
                if (temp==root.left){
                    temp=root.right;
                }
                else {
                    temp=root.left;
                }
                if (temp==null){
                    temp=root;
                    root=null;
                }
                else{
                    root=temp;
                }
            }
            else{
                Product successor = minValueProduct(root.right);
                Product newRight = deleteProduct(root.right, successor.getID());
                successor.left = root.left;
                successor.right = newRight;
                root = successor;
            }
        }
        if (root==null)
            return root;
        root.setHight(max(hight(root.left),hight(root.right))+1);

        int Balance=getBalance(root);

        if (Balance >1 && getBalance(root.left)>= 0){
            return RightRotate(root);
        }
        if (Balance <-1 && getBalance(root.right)<=0){
            return LeftRotate(root);
        }
        if (Balance>1 && getBalance(root.left)<0){
            root.left=LeftRotate(root.left);
            return RightRotate(root);
        }
        if (Balance<-1 && getBalance(root.right)>0 ){
            root.right=RightRotate(root.right);
            return LeftRotate(root);
        }

        return root;
    }

    Product searchProductByID(Product root,int ID){
        if (root==null){
            return null;
        }
        if (root.getID()==ID){
            return root;
        } else if (ID<root.getID()) {
            return searchProductByID(root.left,ID);
        } else if (ID > root.getID()) {
            return searchProductByID(root.right,ID);
        }
        else
            return null;
    }

    void inOrder(Product root){
        if (root!=null){
            inOrder(root.left);
            System.out.println(root+" ");
            inOrder(root.right);
        }
    }


}

