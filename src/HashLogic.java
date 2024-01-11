import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;
import java.lang.reflect.*;

public class HashLogic {

    static Object table;

    public static JTree printMapParams(Map initMap, JTree tree, DefaultMutableTreeNode top) throws NoSuchFieldException, IllegalAccessException{
        Set<Map.Entry<Fruit, Integer>> ent = initMap.entrySet();
        Object[] meArr =  ent.toArray();

        DefaultMutableTreeNode node = null;

        Class mapClass = initMap.getClass();
        Field nodeArr = mapClass.getDeclaredField("table");
        nodeArr.setAccessible(true);
        table = nodeArr.get(initMap);

        for(Object tableNode : (Object[]) table){
            if(tableNode!=null){
                addNextOrExit(tableNode, top);
            } else {
                node = new DefaultMutableTreeNode(("empty bucket"));
                top.add(node);
            }
        }

        return tree;
        /*
          Field hash = objgetDeclaredField("hash");
          Field next = obj.getDeclaredField("next");
          Field key = obj.getDeclaredField("key");
          Field value = obj.getDeclaredField("value");
        value.setAccessible(true); */
    }

    private static void addNextOrExit(Object tnField, DefaultMutableTreeNode treeNode ) throws NoSuchFieldException, IllegalAccessException {
        if (tnField==null){
            return;
        }
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        Class obj = tnField.getClass();

        Field tnFieldNextField = null;

        try{
            tnFieldNextField = obj.getDeclaredField("next");

        } catch (NoSuchFieldException e){
            /*
             *******************************************************
             *************_____tree nodes____***********************
             *******************************************************
             */
            System.out.println("possibly a treenode "+tnField.toString());
            String keySimpleName=null;
            String valueSimpleName=null;

            Field leftTree = obj.getDeclaredField("left");;
            Field rightTree = obj.getDeclaredField("right");

            leftTree.setAccessible(true);
            rightTree.setAccessible(true);

            Object leftVal = leftTree.get(tnField);
            Object rightVal = rightTree.get(tnField);

            Class<?> parentObj = obj.getSuperclass();
            Field[] fieldsOfNode = parentObj.getDeclaredFields();

            Class<?> parentParentObj = parentObj.getSuperclass();

            Field[] fieldsOfTreeNode = obj.getDeclaredFields(); //parent left right prev red assertionDisabled
            Field[] fieldsPred = parentParentObj.getDeclaredFields(); //hash key value next
            Field[] fields = Arrays.copyOf(fieldsOfTreeNode, fieldsOfTreeNode.length + fieldsPred.length);
            System.arraycopy(fieldsPred, 0, fields, fieldsOfTreeNode.length, fieldsPred.length);

            StringBuilder nodeSB = new StringBuilder();
            nodeSB.append("I'm a treeNode!").append(System.lineSeparator());

            for(Field f : fields){
                nodeSB.append(f.getName());
                f.setAccessible(true);

                if(f.getName().equals("key")){
                    //for extensive inside info
                    if(f.get(tnField)==null){
                        //key might be null
                        nodeSB.append("{ null }");
                    } else {
                        keySimpleName = f.get(tnField).toString();
                        Field insideObject = parentParentObj.getDeclaredField("key");
                        insideObject.setAccessible(true);
                        Object insiddee = insideObject.get(tnField);

                        Class insideCl = insiddee.getClass();
                        Field[] fieldsOfInsideClass = insideCl.getDeclaredFields();
                        StringBuilder keyObjSB = new StringBuilder();
                        keyObjSB.append("{");
                        keyObjSB.append(System.lineSeparator());
                        keyObjSB.append(" classname : ").append(insideCl.getSimpleName()).append(", ");
                        for(Field ff : fieldsOfInsideClass){
                            ff.setAccessible(true);
                            keyObjSB.append(ff.getName()).append(" : ").append(ff.get(insiddee)).append(", ").append(System.lineSeparator());
                        }
                        if(!keyObjSB.toString().contains("hash")){
                            keyObjSB.append("hash: "+insiddee.hashCode()).append(System.lineSeparator());
                        }

                        //(length-1)&hash
                        keyObjSB.append("attribute of the same bucket: ((length-1)&hash): "+((((Object[]) table).length-1) & insiddee.hashCode()));
                        keyObjSB.append(System.lineSeparator());

                        keyObjSB.append(" }");
                        //System.out.println(keyObjSB.toString());
                        nodeSB.append(" : ").append(keyObjSB.toString()).append(" ");
                    }
                } else{
                    nodeSB.append(" : ").append(f.get(tnField)).append(" ");
                }

                if(f.getName().equals("value")){
                    valueSimpleName=f.get(tnField).toString();
                }

                nodeSB.append(System.lineSeparator());
            } //for


            String finalKeySimpleName = keySimpleName;
            String finalValueSimpleName = valueSimpleName;
            ArrayList<String> o = new ArrayList<String>(){
                @Override
                public String toString(){
                    return ""+ finalKeySimpleName +" -> "+finalValueSimpleName;
                }
            };
            o.add(nodeSB.toString());

            node.setUserObject(o);
            treeNode.add(node);
            addNextOrExit(leftVal, node);
            addNextOrExit(rightVal, node);
            return;
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        /*
        *******************************************************
        **********_____Usual liked nodes____*******************
        *******************************************************
        */

        tnFieldNextField.setAccessible(true);
        Object next = tnFieldNextField.get(tnField);

        String keySimpleName=null;
        String valueSimpleName=null;

        Field[] fields = obj.getDeclaredFields();
        StringBuilder nodeSB = new StringBuilder();
        if (next!=null){
            nodeSB.append("I'm a linkedNode!").append(System.lineSeparator());
        }

        for(Field f : fields){
            nodeSB.append(f.getName());
            f.setAccessible(true);

            if(f.getName().equals("key")){
                //for extensive inside info
                if(f.get(tnField)==null){
                    //key might be null
                    nodeSB.append("{ null }");
                } else {
                    keySimpleName = f.get(tnField).toString();
                    Field insideObject = obj.getDeclaredField("key");
                    insideObject.setAccessible(true);
                    Object insiddee = insideObject.get(tnField);

                    Class insideCl = insiddee.getClass();
                    Field[] fieldsOfInsideClass = insideCl.getDeclaredFields();
                    StringBuilder keyObjSB = new StringBuilder();
                    keyObjSB.append("{");
                    keyObjSB.append(System.lineSeparator());
                    keyObjSB.append(" classname : ").append(insideCl.getSimpleName()).append(", ");
                    for(Field ff : fieldsOfInsideClass){
                        ff.setAccessible(true);
                        keyObjSB.append(ff.getName()).append(" : ").append(ff.get(insiddee)).append(", ").append(System.lineSeparator());
                    }
                    if(!keyObjSB.toString().contains("hash")){
                        keyObjSB.append("hash: "+insiddee.hashCode()).append(System.lineSeparator());
                    }

                    //(length-1)&hash
                    keyObjSB.append("attribute of the same bucket: ((length-1)&hash): "+((((Object[]) table).length-1) & insiddee.hashCode()));
                    keyObjSB.append(System.lineSeparator());

                    keyObjSB.append(" }");
                    //System.out.println(keyObjSB.toString());
                    nodeSB.append(" : ").append(keyObjSB.toString()).append(" ");
                }
            } else{
                nodeSB.append(" : ").append(f.get(tnField)).append(" ");
            }

            if(f.getName().equals("value")){
                valueSimpleName=f.get(tnField).toString();
            }

            nodeSB.append(System.lineSeparator());
        } //for


        String finalKeySimpleName = keySimpleName;
        String finalValueSimpleName = valueSimpleName;
        ArrayList<String> o = new ArrayList<String>(){
            @Override
            public String toString(){
                return ""+ finalKeySimpleName +" -> "+finalValueSimpleName;
            }
        };
        o.add(nodeSB.toString());

        node.setUserObject(o);
        treeNode.add(node);
        addNextOrExit(next, node);
    }
    
}
