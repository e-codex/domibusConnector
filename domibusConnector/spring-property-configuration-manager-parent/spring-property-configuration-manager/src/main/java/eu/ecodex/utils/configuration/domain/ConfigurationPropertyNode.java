package eu.ecodex.utils.configuration.domain;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigurationPropertyNode {

    private Map<String, ConfigurationPropertyNode> children = new HashMap<>();

    /**
     * The root node will have an empty string as name
     */
    private String nodeName = "";

    @Nullable
    private ConfigurationProperty property;

    /**
     * This is the link to the parent node
     * it is null if it is the root node
     */
    @Nullable
    private ConfigurationPropertyNode parent;

    public void addChild(ConfigurationPropertyNode propertyNode) {
        String name = propertyNode.getNodeName();
        this.children.put(name, propertyNode);
        propertyNode.setParent(this);
        //TODO: check property name ein propertyConfig
    }

    public Collection<ConfigurationPropertyNode> getChildren() {
        return this.children.values();
    }

    public ConfigurationPropertyNode removeChild(ConfigurationPropertyNode node) {
        return removeChild(node.getNodeName());
    }

    public ConfigurationPropertyNode removeChild(String name) {
        return this.children.remove(name);
    }

    public ConfigurationPropertyNode getParent() {
        return parent;
    }

    public void setParent(ConfigurationPropertyNode parent) {
        this.parent = parent;
    }

    @Nullable
    public ConfigurationProperty getProperty() {
        return property;
    }

    public void setProperty(@Nullable ConfigurationProperty property) {
        this.property = property;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getFullNodePath() {
        String nodePath = "";
        Stack<String> familyTree = new Stack<>();
        ConfigurationPropertyNode node = this;
        do {
            familyTree.push(node.getNodeName());
            node = node.getParent();
        } while (node != null && !StringUtils.isEmpty(node.getNodeName()) );
        //pop empty root node
//        familyTree.pop();
        if (!familyTree.empty()) {
            nodePath = familyTree.pop();
        }
        while (!familyTree.empty()) {
            String nodeName = familyTree.pop();
            nodePath = nodePath + "." + nodeName;
        }
        return nodePath;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public boolean isRootNode() {
        return "".equals(nodeName);
    }

    public Optional<ConfigurationPropertyNode> getChild(String name) {
        return Optional.ofNullable(this.children.get(name));
    }
}
