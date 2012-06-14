package org.holodeck.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * @author Hamid Ben Malek
 */
@Root(name="Property", strict=false)
public class Property implements java.io.Serializable
{
  private static final long serialVersionUID = 3026385474435552009L;  

  @Attribute
  protected String name;

  @Attribute(required=false)
  protected String type;

  @Attribute(required=false)
  protected String description;

  @Attribute(required=false)
  protected boolean required = false;

  public Property() {}
  public Property(String name, String type, String desc, boolean required)
  {
    this.name = name;
    this.type = type;
    this.description = desc;
    this.required = required;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

  public String getDescription() { return description; }
  public void setDescription(String description)
  {
    this.description = description;
  }

  public boolean isRequired() { return required; }
  public void setRequired(boolean required) { this.required = required; }
}