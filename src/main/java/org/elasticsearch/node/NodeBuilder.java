begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|node
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|ImmutableSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_comment
comment|/**  * A node builder is used to construct a {@link Node} instance.  *<p/>  *<p>Settings will be loaded relative to the ES home (with or without<tt>config/</tt> prefix) and if not found,  * within the classpath (with or without<tt>config/<tt> prefix). The settings file loaded can either be named  *<tt>elasticsearch.yml</tt> or<tt>elasticsearch.json</tt>). Loading settings can be disabled by calling  * {@link #loadConfigSettings(boolean)} with<tt>false<tt>.  *<p/>  *<p>Explicit settings can be passed by using the {@link #settings(Settings)} method.  *<p/>  *<p>In any case, settings will be resolved from system properties as well that are either prefixed with<tt>es.</tt>  * or<tt>elasticsearch.</tt>.  *<p/>  *<p>An example for creating a simple node with optional settings loaded from the classpath:  *<p/>  *<pre>  * Node node = NodeBuilder.nodeBuilder().node();  *</pre>  *<p/>  *<p>An example for creating a node with explicit settings (in this case, a node in the cluster that does not hold  * data):  *<p/>  *<pre>  * Node node = NodeBuilder.nodeBuilder()  *                      .settings(ImmutableSettings.settingsBuilder().put("node.data", false)  *                      .node();  *</pre>  *<p/>  *<p>When done with the node, make sure you call {@link Node#close()} on it.  *  *  */
end_comment

begin_class
DECL|class|NodeBuilder
specifier|public
class|class
name|NodeBuilder
block|{
DECL|field|settings
specifier|private
specifier|final
name|ImmutableSettings
operator|.
name|Builder
name|settings
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
decl_stmt|;
DECL|field|loadConfigSettings
specifier|private
name|boolean
name|loadConfigSettings
init|=
literal|true
decl_stmt|;
comment|/**      * A convenient factory method to create a {@link NodeBuilder}.      */
DECL|method|nodeBuilder
specifier|public
specifier|static
name|NodeBuilder
name|nodeBuilder
parameter_list|()
block|{
return|return
operator|new
name|NodeBuilder
argument_list|()
return|;
block|}
comment|/**      * Set addition settings simply by working directly against the settings builder.      */
DECL|method|settings
specifier|public
name|ImmutableSettings
operator|.
name|Builder
name|settings
parameter_list|()
block|{
return|return
name|settings
return|;
block|}
comment|/**      * Set addition settings simply by working directly against the settings builder.      */
DECL|method|getSettings
specifier|public
name|ImmutableSettings
operator|.
name|Builder
name|getSettings
parameter_list|()
block|{
return|return
name|settings
return|;
block|}
comment|/**      * Explicit node settings to set.      */
DECL|method|settings
specifier|public
name|NodeBuilder
name|settings
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|)
block|{
return|return
name|settings
argument_list|(
name|settings
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Explicit node settings to set.      */
DECL|method|settings
specifier|public
name|NodeBuilder
name|settings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|.
name|put
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node builder automatically try and load config settings from the file system / classpath. Defaults      * to<tt>true</tt>.      */
DECL|method|loadConfigSettings
specifier|public
name|NodeBuilder
name|loadConfigSettings
parameter_list|(
name|boolean
name|loadConfigSettings
parameter_list|)
block|{
name|this
operator|.
name|loadConfigSettings
operator|=
name|loadConfigSettings
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Is the node going to be a client node which means it will hold no data (<tt>node.data</tt> is      * set to<tt>false</tt>) and other optimizations by different modules.      *      * @param client Should the node be just a client node or not.      */
DECL|method|client
specifier|public
name|NodeBuilder
name|client
parameter_list|(
name|boolean
name|client
parameter_list|)
block|{
name|settings
operator|.
name|put
argument_list|(
literal|"node.client"
argument_list|,
name|client
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Is the node going to be allowed to allocate data (shards) to it or not. This setting map to      * the<tt>node.data</tt> setting. Note, when setting {@link #client(boolean)}, the node will      * not hold any data by default.      *      * @param data Should the node be allocated data to or not.      */
DECL|method|data
specifier|public
name|NodeBuilder
name|data
parameter_list|(
name|boolean
name|data
parameter_list|)
block|{
name|settings
operator|.
name|put
argument_list|(
literal|"node.data"
argument_list|,
name|data
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Is the node a local node. A local node is a node that uses a local (JVM level) discovery and      * transport. Other (local) nodes started within the same JVM (actually, class-loader) will be      * discovered and communicated with. Nodes outside of the JVM will not be discovered.      *      * @param local Should the node be local or not      */
DECL|method|local
specifier|public
name|NodeBuilder
name|local
parameter_list|(
name|boolean
name|local
parameter_list|)
block|{
name|settings
operator|.
name|put
argument_list|(
literal|"node.local"
argument_list|,
name|local
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The cluster name this node is part of (maps to the<tt>cluster.name</tt> setting). Defaults      * to<tt>elasticsearch</tt>.      *      * @param clusterName The cluster name this node is part of.      */
DECL|method|clusterName
specifier|public
name|NodeBuilder
name|clusterName
parameter_list|(
name|String
name|clusterName
parameter_list|)
block|{
name|settings
operator|.
name|put
argument_list|(
literal|"cluster.name"
argument_list|,
name|clusterName
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Builds the node without starting it.      */
DECL|method|build
specifier|public
name|Node
name|build
parameter_list|()
block|{
return|return
operator|new
name|Node
argument_list|(
name|settings
operator|.
name|build
argument_list|()
argument_list|,
name|loadConfigSettings
argument_list|)
return|;
block|}
comment|/**      * {@link #build()}s and starts the node.      */
DECL|method|node
specifier|public
name|Node
name|node
parameter_list|()
block|{
return|return
name|build
argument_list|()
operator|.
name|start
argument_list|()
return|;
block|}
block|}
end_class

end_unit

