package proj2ab;

import java.util.List;

public class KDTree {
    private Node rootNode;
    final boolean HORIZONTAL = false;
    final boolean VERTICAL = true;

    private class Node {
        private Point point;
        private boolean direction;      // true for horizontal, false for vertical
        private Node leftDown, rightUp;

        Node(Point point, boolean direction) {
            this.point = point;
            this.direction = direction;
        }
    }

    public KDTree() {
    }
    public KDTree(List<Point> points) {
        for (Point point : points) {
            insert(point);
        }
    }

    public void insert(Point point) {
        if (point == null) {
            throw new IllegalArgumentException();
        }
        rootNode = insert(rootNode, HORIZONTAL, point);
    }

    private Node insert(Node node, boolean direction, Point point) {
        // direction indicate the node direction for the new node
        if (node == null) {
            return new Node(point, direction);
        }
        if (node.direction == VERTICAL) {
            if (point.getX() < node.point.getX()) {
                node.leftDown = insert(node.leftDown, HORIZONTAL, point);
            } else {
                node.rightUp = insert(node.rightUp, HORIZONTAL, point);
            }
        } else {
            if (point.getY() < node.point.getY()) {
                node.leftDown = insert(node.leftDown, VERTICAL, point);
            } else {
                node.rightUp = insert(node.rightUp, VERTICAL, point);
            }
        }
        return node;
    }

    public Point nearest(double x, double y) {
        return nearest(rootNode, new Point(x, y), rootNode).point;
    }
//    public Point nearest1(double x, double y){
//        return nearest(rootNode, new Point(x, y));
//    }

    private Node nearest(Node n, Point goal, Node best) {
        Node goodSide, badSide;
        if (n == null) {
            return best;
        }
        if (Point.distance(n.point, goal) < Point.distance(best.point, goal)) {
            best = n;
        }
        if ((n.direction == VERTICAL && goal.getX() - n.point.getX() > 0)
            || (n.direction == HORIZONTAL && goal.getY() - n.point.getY() > 0)) {
            goodSide =  n.rightUp;
            badSide = n.leftDown;
        } else {
            goodSide =  n.leftDown;
            badSide = n.rightUp;
        }
        best = nearest(goodSide, goal, best);
        double verticalBestDistanceBad
                = Point.distance(new Point(n.point.getX(), goal.getY()), goal);
        double horizontalBestDistanceBad
                = Point.distance(new Point(goal.getX(), n.point.getY()), goal);
        if ((n.direction == VERTICAL && verticalBestDistanceBad < Point.distance(best.point, goal))
                || (n.direction == HORIZONTAL
                && horizontalBestDistanceBad < Point.distance(best.point, goal))) {
            best = nearest(badSide, goal, best);
        }
        return best;
    }
//    private Point nearest(Node node, Point input){
//        Point result;
//        Point minPoint = node.point;
//        double minDistance;
//        if(node.leftDown == null && node.rightUp != null){
//            minPoint = nearest(node.rightUp, input);
//        }else if(node.leftDown != null && node.rightUp == null){
//            minPoint = nearest(node.leftDown, input);
//        }else if(node.leftDown != null && node.rightUp != null){
//            Point minLeftDown = nearest(node.leftDown, input);
//            Point minRightUp = nearest(node.rightUp, input);
//            if(Point.distance(minLeftDown, input) >= Point.distance(minRightUp, input)){
//                minPoint = minRightUp;
//            }else {
//                minPoint = minLeftDown;
//            }
//        }
//
//        if(Point.distance(minPoint, input) >= Point.distance(node.point, input)){
//            return node.point;
//        }else {
//            return minPoint;
//        }
//    }
}
