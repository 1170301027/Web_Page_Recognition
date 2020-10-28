package org.example.parse;

import org.example.kit.ByteBuffer;
import org.example.kit.StringKit;
import org.example.kit.entity.ByteArray;
import org.example.parse.nodes.Document;
import org.example.parse.nodes.Element;
import org.example.parse.nodes.Node;
import org.example.parse.nodes.TextNode;

import java.util.*;
import java.util.function.Consumer;

/**
 * @Classname Parser
 * @Description
 * @Date 2020/10/28 10:33
 * @Created by shuaif
 */
public class Parser {

    private static final byte TAG_START_FLAG = '<';
    private static final byte TAG_END_FLAG = '>';
    private static final byte TAG_CLOSING_FLAG = '/';
    private static final byte[] COMMENT_START_FLAG = new byte[]{'<', '!', '-', '-'};
    private static final byte[] COMMENT_END_FLAG = new byte[]{'-', '-', '>'};

    public static final int MAX_PARSING_DEPTH = 6;

    private int maxParsingDepth = MAX_PARSING_DEPTH;
    private ByteBuffer buffer;
    private final Map<String,Tag> localTagMap = new HashMap<>(32);

    public Parser(byte[] source, int from, int to){
        buffer = ByteBuffer.wrap(source, from, to - from);
    }

    public Parser(byte[] source){
        this(source, 0, source.length);
    }

    public Parser(ByteArray array){
        this(array.getParentBytes(), array.from(), array.to());
    }

    private Tag getTag(String tagName){
        Tag tag = localTagMap.get(tagName);
        if(tag != null)
            return tag;

        tag = Tag.getTag(tagName);
        localTagMap.put(tagName, tag);
        return tag;
    }

    public void addAction(String tagName, Consumer<Node> action){
        getTag(tagName).setAction(action);
    }

    public Document parse(){
        buffer.reset();
        Document document = new Document();
        parseDOCTYPE();
        document.setHtml(parseHTML());
        return document;
    }

    private void parseDOCTYPE(){
        // doctype解析
    }

    private Element parseHTML(){
        Element html = new Element(getTag("html"));
        int count = 0;
        try{
            boolean findHTML = false;
            while(true){
                if(!buffer.hasNext(TAG_START_FLAG))
                    break;
                String name = getTagName();
                if(name == null){
                    break;
                }else if(!name.equals("html")){
                    if(name.charAt(0) == TAG_CLOSING_FLAG){
                        if(buffer.noNext(TAG_END_FLAG))
                            break;
                        continue;
                    }
                    Tag tag = getTag(name);
                    Element e = new Element(tag, html, count++);
                    html.appendChild(e);
                    e.setAttrs(getAttributesFromTag());
                    if(!tag.isEmpty())
                        parse(e);
                    if(!buffer.goNext())
                        break;
                }else{
                    findHTML = true;
                    break;
                }
            }
            if(findHTML){
                html.setAttrs(getAttributesFromTag());
                parse(html);
            }
            handleHTMLStructure(html);
        }catch(Exception e){
            if(handleHTMLStructure(html)){
                // ...
            }else{
                throw e;
            }
        }
        return html;
    }

    private boolean handleHTMLStructure(Element html){
        Element head = html.childElement("head");
        Element body = html.childElement("body");
        int size = html.children().size();
        if(size == 0)
            return false;

        if(head == null){
            int index;
            if(body != null && (index = body.indexOfElement("head")) >= 0){
                head = (Element)body.removeChild(index);
            }else
                head = new Element(getTag("head"), html, 0);
            html.appendChild(0, head);
            size++;
        }
        if(body == null){
            int index;
            if((index = head.indexOfElement("body")) >= 0){
                body = (Element)head.removeChild(index);
            }else
                body = new Element(getTag("body"), html, 1);
            html.appendChild(html.childrenSize(), body);
            size++;
        }
        if(size == 2)
            return true;
        for(Iterator<Node> it = html.children().iterator(); it.hasNext(); ){
            Node child = it.next();
            if(child == head || child == body){
                continue;
            }
            if(child instanceof Element && ((Element)child).getTag().isInHead()){
                head.appendChild(child);
                child.parent(head);
            }else{
                body.appendChild(child);
                child.parent(head);
            }
            child.updateIndex();
            it.remove();
        }
        if(body.index() == 0){
            html.appendChild(html.removeChild(body));
            body.updateIndex();
        }
        head.updateIndex();
        return true;
    }

    // TODO 非递归方式...
    private Tag parse(Element parent){
        Tag pTag = parent.getTag();
        if(pTag.isNoSubTag() || pTag.getName().equals("script")){
            return parseTextTag(parent);
        }
        boolean parseAll = parent.getDepth() < maxParsingDepth;
        while(true){
            if(!buffer.goNext())
                break;
            int siblings = parent.children().size();
            if(parseAll){
                int textFrom = buffer.position();
                buffer.moveToUnblankChar();
                if(buffer.get() != TAG_START_FLAG){ // 文本节点
                    TextNode text = new TextNode(buffer.wrapToByteArray(textFrom, buffer.moveTo(TAG_START_FLAG)), parent, siblings++);
                    parent.appendChild(text);
                }
            }else{
                buffer.moveToUnblankChar();
                if(buffer.get() != TAG_START_FLAG){ // 文本节点
                    buffer.moveTo(TAG_START_FLAG);
                }
            }
            String name;
            while(true){
                name = getTagName();
                if(name.charAt(0) == TAG_CLOSING_FLAG){
                    buffer.moveTo(TAG_END_FLAG);
                    String tag = name.substring(1);
                    if(tag.equals(pTag.getName()))
                        return null;
                    else{
                        Tag errTag = getTag(tag);
                        if(parent.getParentByTag(errTag) != null)
                            return errTag;
                    }
                }else{
                    break;
                }
            }
            Tag tag = getTag(name), endTag = null;
            Element e = new Element(tag, parent, siblings);
            Consumer<Node> action = tag.getAction();
            if(parseAll || action != null){
                e.setAttrs(getAttributesFromTag()); // 解析该元素的属性
                if(parseAll){
                    parent.appendChild(e);
                }
            }else{
                buffer.moveTo(TAG_END_FLAG);
            }
            if(action != null)
                action.accept(e);
            if(!tag.isEmpty()) // 解析非自关闭标签
                endTag = parse(e);
            if(endTag != null){ // 解决标签闭合错误的问题
                if(endTag == parent.getTag())
                    break;
                else
                    return endTag;
            }
        }
        return null;
    }

    private Tag parseTextTag(Element parent){
        if(!buffer.goNext())
            return null;
        int textFrom = buffer.position();
        while(true){
            buffer.moveAfter(TAG_START_FLAG);
            if(buffer.get() != '/')
                continue;
            buffer.next();
            int textEnd = buffer.position();
            byte[] bs = buffer.copyOfRange(textEnd, buffer.moveUntilBlankCharOr(TAG_END_FLAG));
            StringKit.toLowerCase(bs);
            String tagName = new String(bs);
            if(tagName.equals(parent.getTagName())){
                if(textFrom < textEnd - 2){
                    TextNode text = new TextNode(buffer.wrapToByteArray(textFrom, textEnd - 2), parent, 0);
                    parent.appendChild(text);
                }
                return null;
            }
        }
    }

//    // 深度大于 MAX_PARSING_DEPTH 时，只进行解析，不创建元素
//    private String parse(String parent){
//        while(true){
//            buffer.moveToUnblankChar();
//            if(buffer.get() != TAG_START_FLAG){ // 文本节点
//                buffer.moveTo(TAG_START_FLAG);
//                continue;
//            }
//            String name = getTagName();
//            if(name.charAt(0) == TAG_CLOSING_FLAG){
//                buffer.moveAfter(TAG_END_FLAG);
//                String tag = name.substring(1);
//                if(tag.equals(parent)){
//                    break;
//                }else{
//                    throw new IllegalArgumentException("错误的闭合标签：" + name);
//                }
//            }
//            Tag tag = Tag.valueOf(name);
//            buffer.moveAfter(TAG_END_FLAG);
//            if(!tag.isEmpty())
//                parse(name);
//        }
//        return null;
//    }

    private String getTagName(){
        if(buffer.noNext(TAG_START_FLAG))
            return null;
        byte v;
        while((v = buffer.get()) == '!' || v == '?'){ // TODO 注释的解析，getTagName方法优化
            if(buffer.getNext() == '-')
                buffer.moveTo(COMMENT_END_FLAG);
            buffer.moveTo(TAG_END_FLAG);
            if(buffer.noNext(TAG_START_FLAG))
                return null;
        }
        int from = buffer.moveToUnblankChar();
        buffer.moveUntilBlankCharOr(TAG_END_FLAG);
        byte[] bs = buffer.copyOfRange(from, buffer.position());
        for(int i = 0; i < bs.length; i++){
            v = bs[i];
            if(v >= 'A' && v <= 'Z'){
                v = (byte)(v + 32);
                bs[i] = v;
            }
        }
        return new String(bs);
    }

    // TODO 可以优化下
    private Attribute[] getAttributesFromTag(){
        buffer.moveToUnblankChar();
        if(buffer.get() == '>'){
            return null;
        }
        byte[] arr = buffer.array();
        List<Attribute> result = new LinkedList<>();
        while(true){
            int from = buffer.moveToUnblankChar(), pos = from;
            byte c;
            while((c = arr[pos]) > 32 && c != '=' && c != '>' && c != '/') // TODO 简化
                pos++;
            if(pos == from)
                break;
            Attribute attr = new Attribute();
            result.add(attr);
            attr.key = buffer.copyOfRange(from, pos);

            buffer.position(pos);
            pos = buffer.moveToUnblankChar();
            if((c = arr[pos]) == '>' || c == '/')
                break;
            if(c != '=')
                continue;
            buffer.next();
            pos = buffer.moveToUnblankChar();
            if((c = arr[pos]) == '"' || c == '\''){
                from = pos + 1;
                buffer.next();
                attr.value = buffer.wrapToByteArray(from, buffer.moveTo(c));
                buffer.next();
            }else{
                from = pos;
                attr.value = buffer.wrapToByteArray(from, buffer.moveUntilBlankCharOr(TAG_END_FLAG));
            }
        }
        buffer.moveTo(TAG_END_FLAG);
        return result.toArray(new Attribute[result.size()]);
    }

    public int getMaxParsingDepth(){
        return maxParsingDepth;
    }
    public void setMaxParsingDepth(int maxParsingDepth){
        this.maxParsingDepth = maxParsingDepth;
    }
}
