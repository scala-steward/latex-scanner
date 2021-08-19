package io.doerfler.latex

object Progress {
  def within(sections: Seq[Section]): Double = {
    def factorDoneWeighted =
      sections
        .filter(_.status == "done")
        .map(weightOfSectionWithRespectToSectionsOfSameFile)
        .sum
    def weightOfSectionWithRespectToSectionsOfSameFile(s: Section) =
      s.content.length / charCountInFile.toDouble
    lazy val charCountInFile = sections.map(_.content.length).sum

    factorDoneWeighted
  }
}
