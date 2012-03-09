begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.common.lucene.manager
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|manager
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|IndexSearcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Factory class used by {@link org.apache.lucene.search.SearcherManager} and {@link org.apache.lucene.search.NRTManager} to  * create new IndexSearchers. The default implementation just creates  * an IndexSearcher with no custom behavior:  *<p/>  *<pre class="prettyprint">  * public IndexSearcher newSearcher(IndexReader r) throws IOException {  * return new IndexSearcher(r);  * }  *</pre>  *<p/>  * You can pass your own factory instead if you want custom behavior, such as:  *<ul>  *<li>Setting a custom scoring model: {@link org.apache.lucene.search.IndexSearcher#setSimilarity(org.apache.lucene.search.Similarity)}  *<li>Parallel per-segment search: {@link org.apache.lucene.search.IndexSearcher#IndexSearcher(org.apache.lucene.index.IndexReader, java.util.concurrent.ExecutorService)}  *<li>Return custom subclasses of IndexSearcher (for example that implement distributed scoring)  *<li>Run queries to warm your IndexSearcher before it is used. Note: when using near-realtime search  * you may want to also {@link org.apache.lucene.index.IndexWriterConfig#setMergedSegmentWarmer(org.apache.lucene.index.IndexWriter.IndexReaderWarmer)} to warm  * newly merged segments in the background, outside of the reopen path.  *</ul>  *  * @lucene.experimental  */
end_comment

begin_comment
comment|// LUCENE MONITOR: 3.6 Remove this once 3.6 is out and use it
end_comment

begin_class
DECL|class|SearcherFactory
specifier|public
class|class
name|SearcherFactory
block|{
comment|/**      * Returns a new IndexSearcher over the given reader.      */
DECL|method|newSearcher
specifier|public
name|IndexSearcher
name|newSearcher
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
return|;
block|}
block|}
end_class

end_unit

