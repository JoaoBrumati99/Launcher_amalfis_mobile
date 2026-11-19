$path = "C:\Users\Joao\Downloads\Documentacao_Geral_Amalfis_Mobile (1).docx"
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [IO.Compression.ZipFile]::OpenRead($path)
$entry = $zip.GetEntry("word/document.xml")
$sr = New-Object System.IO.StreamReader($entry.Open())
$text = $sr.ReadToEnd()
$sr.Dispose()
$zip.Dispose()
[xml]$xml = $text
$ns = New-Object System.Xml.XmlNamespaceManager($xml.NameTable)
$ns.AddNamespace("w", "http://schemas.openxmlformats.org/wordprocessingml/2006/main")
$nodes = $xml.SelectNodes("//w:t", $ns)
foreach ($node in $nodes) {
    Write-Output $node.InnerText
}
