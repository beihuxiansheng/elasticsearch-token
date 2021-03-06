begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.uhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|uhighlight
package|;
end_package

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
name|highlight
operator|.
name|DefaultEncoder
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
name|highlight
operator|.
name|SimpleHTMLEncoder
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|notNullValue
import|;
end_import

begin_class
DECL|class|CustomPassageFormatterTests
specifier|public
class|class
name|CustomPassageFormatterTests
extends|extends
name|ESTestCase
block|{
DECL|method|testSimpleFormat
specifier|public
name|void
name|testSimpleFormat
parameter_list|()
block|{
name|String
name|content
init|=
literal|"This is a really cool highlighter. Unified highlighter gives nice snippets back. No matches here."
decl_stmt|;
name|CustomPassageFormatter
name|passageFormatter
init|=
operator|new
name|CustomPassageFormatter
argument_list|(
literal|"<em>"
argument_list|,
literal|"</em>"
argument_list|,
operator|new
name|DefaultEncoder
argument_list|()
argument_list|)
decl_stmt|;
name|Passage
index|[]
name|passages
init|=
operator|new
name|Passage
index|[
literal|3
index|]
decl_stmt|;
name|String
name|match
init|=
literal|"highlighter"
decl_stmt|;
name|BytesRef
name|matchBytesRef
init|=
operator|new
name|BytesRef
argument_list|(
name|match
argument_list|)
decl_stmt|;
name|Passage
name|passage1
init|=
operator|new
name|Passage
argument_list|()
decl_stmt|;
name|int
name|start
init|=
name|content
operator|.
name|indexOf
argument_list|(
name|match
argument_list|)
decl_stmt|;
name|int
name|end
init|=
name|start
operator|+
name|match
operator|.
name|length
argument_list|()
decl_stmt|;
name|passage1
operator|.
name|setStartOffset
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|passage1
operator|.
name|setEndOffset
argument_list|(
name|end
operator|+
literal|2
argument_list|)
expr_stmt|;
comment|//lets include the whitespace at the end to make sure we trim it
name|passage1
operator|.
name|addMatch
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|matchBytesRef
argument_list|)
expr_stmt|;
name|passages
index|[
literal|0
index|]
operator|=
name|passage1
expr_stmt|;
name|Passage
name|passage2
init|=
operator|new
name|Passage
argument_list|()
decl_stmt|;
name|start
operator|=
name|content
operator|.
name|lastIndexOf
argument_list|(
name|match
argument_list|)
expr_stmt|;
name|end
operator|=
name|start
operator|+
name|match
operator|.
name|length
argument_list|()
expr_stmt|;
name|passage2
operator|.
name|setStartOffset
argument_list|(
name|passage1
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
name|passage2
operator|.
name|setEndOffset
argument_list|(
name|end
operator|+
literal|26
argument_list|)
expr_stmt|;
name|passage2
operator|.
name|addMatch
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|matchBytesRef
argument_list|)
expr_stmt|;
name|passages
index|[
literal|1
index|]
operator|=
name|passage2
expr_stmt|;
name|Passage
name|passage3
init|=
operator|new
name|Passage
argument_list|()
decl_stmt|;
name|passage3
operator|.
name|setStartOffset
argument_list|(
name|passage2
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
name|passage3
operator|.
name|setEndOffset
argument_list|(
name|content
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|passages
index|[
literal|2
index|]
operator|=
name|passage3
expr_stmt|;
name|Snippet
index|[]
name|fragments
init|=
name|passageFormatter
operator|.
name|format
argument_list|(
name|passages
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fragments
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fragments
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fragments
index|[
literal|0
index|]
operator|.
name|getText
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"This is a really cool<em>highlighter</em>."
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fragments
index|[
literal|0
index|]
operator|.
name|isHighlighted
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fragments
index|[
literal|1
index|]
operator|.
name|getText
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"Unified<em>highlighter</em> gives nice snippets back."
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fragments
index|[
literal|1
index|]
operator|.
name|isHighlighted
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fragments
index|[
literal|2
index|]
operator|.
name|getText
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"No matches here."
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fragments
index|[
literal|2
index|]
operator|.
name|isHighlighted
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testHtmlEncodeFormat
specifier|public
name|void
name|testHtmlEncodeFormat
parameter_list|()
block|{
name|String
name|content
init|=
literal|"<b>This is a really cool highlighter.</b> Unified highlighter gives nice snippets back."
decl_stmt|;
name|CustomPassageFormatter
name|passageFormatter
init|=
operator|new
name|CustomPassageFormatter
argument_list|(
literal|"<em>"
argument_list|,
literal|"</em>"
argument_list|,
operator|new
name|SimpleHTMLEncoder
argument_list|()
argument_list|)
decl_stmt|;
name|Passage
index|[]
name|passages
init|=
operator|new
name|Passage
index|[
literal|2
index|]
decl_stmt|;
name|String
name|match
init|=
literal|"highlighter"
decl_stmt|;
name|BytesRef
name|matchBytesRef
init|=
operator|new
name|BytesRef
argument_list|(
name|match
argument_list|)
decl_stmt|;
name|Passage
name|passage1
init|=
operator|new
name|Passage
argument_list|()
decl_stmt|;
name|int
name|start
init|=
name|content
operator|.
name|indexOf
argument_list|(
name|match
argument_list|)
decl_stmt|;
name|int
name|end
init|=
name|start
operator|+
name|match
operator|.
name|length
argument_list|()
decl_stmt|;
name|passage1
operator|.
name|setStartOffset
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|passage1
operator|.
name|setEndOffset
argument_list|(
name|end
operator|+
literal|6
argument_list|)
expr_stmt|;
comment|//lets include the whitespace at the end to make sure we trim it
name|passage1
operator|.
name|addMatch
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|matchBytesRef
argument_list|)
expr_stmt|;
name|passages
index|[
literal|0
index|]
operator|=
name|passage1
expr_stmt|;
name|Passage
name|passage2
init|=
operator|new
name|Passage
argument_list|()
decl_stmt|;
name|start
operator|=
name|content
operator|.
name|lastIndexOf
argument_list|(
name|match
argument_list|)
expr_stmt|;
name|end
operator|=
name|start
operator|+
name|match
operator|.
name|length
argument_list|()
expr_stmt|;
name|passage2
operator|.
name|setStartOffset
argument_list|(
name|passage1
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
name|passage2
operator|.
name|setEndOffset
argument_list|(
name|content
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|passage2
operator|.
name|addMatch
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|matchBytesRef
argument_list|)
expr_stmt|;
name|passages
index|[
literal|1
index|]
operator|=
name|passage2
expr_stmt|;
name|Snippet
index|[]
name|fragments
init|=
name|passageFormatter
operator|.
name|format
argument_list|(
name|passages
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fragments
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fragments
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fragments
index|[
literal|0
index|]
operator|.
name|getText
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"&lt;b&gt;This is a really cool<em>highlighter</em>.&lt;&#x2F;b&gt;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fragments
index|[
literal|1
index|]
operator|.
name|getText
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"Unified<em>highlighter</em> gives nice snippets back."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

