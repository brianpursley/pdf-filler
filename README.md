This is a simple web service based on [Apache PDFBox](https://pdfbox.apache.org/), that lets you generate filled PDF forms using a set of values you post to the endpoint.

# Usage

The service provides two endpoints `/` and `/template`.

`/template` generates an example payload for the given fillable PDF.

For example, to generate a template for and [IRS W-9 form](https://www.irs.gov/pub/irs-pdf/fw9.pdf), you can make this request:

```shell script
curl --request POST \
  --url http://localhost:8080/template \
  --header 'content-type: application/json' \
  --data '{
	"url": "https://www.irs.gov/pub/irs-pdf/fw9.pdf"
}'
```

The response is a template containing the URL along with the fields and their default values, like this:
```json
{
  "url": "https://www.irs.gov/pub/irs-pdf/fw9.pdf",
  "values": {
    "topmostSubform[0].Page1[0].f1_1[0]": "",
    "topmostSubform[0].Page1[0].f1_2[0]": "",
    "topmostSubform[0].Page1[0].FederalClassification[0].c1_1[0]": "Off",
    "topmostSubform[0].Page1[0].FederalClassification[0].c1_1[1]": "Off",
    "topmostSubform[0].Page1[0].FederalClassification[0].c1_1[2]": "Off",
    "topmostSubform[0].Page1[0].FederalClassification[0].c1_1[3]": "Off",
    "topmostSubform[0].Page1[0].FederalClassification[0].c1_1[4]": "Off",
    "topmostSubform[0].Page1[0].FederalClassification[0].c1_1[5]": "Off",
    "topmostSubform[0].Page1[0].FederalClassification[0].f1_3[0]": "",
    "topmostSubform[0].Page1[0].FederalClassification[0].c1_1[6]": "Off",
    "topmostSubform[0].Page1[0].FederalClassification[0].f1_4[0]": "",
    "topmostSubform[0].Page1[0].Exemptions[0].f1_5[0]": "",
    "topmostSubform[0].Page1[0].Exemptions[0].f1_6[0]": "",
    "topmostSubform[0].Page1[0].Address[0].f1_7[0]": "",
    "topmostSubform[0].Page1[0].Address[0].f1_8[0]": "",
    "topmostSubform[0].Page1[0].f1_9[0]": "",
    "topmostSubform[0].Page1[0].f1_10[0]": "",
    "topmostSubform[0].Page1[0].SSN[0].f1_11[0]": "",
    "topmostSubform[0].Page1[0].SSN[0].f1_12[0]": "",
    "topmostSubform[0].Page1[0].SSN[0].f1_13[0]": "",
    "topmostSubform[0].Page1[0].EmployerID[0].f1_14[0]": "",
    "topmostSubform[0].Page1[0].EmployerID[0].f1_15[0]": ""
  }
}
```

Once you know the structure of the payload, you can generate a custom payload and post it to the `/` endpoint to generate a filled-in PDF, like this:
```shell script
curl --request POST \
  --url http://localhost:8080/ \
  --header 'content-type: application/json' \
  --data '{
  "url": "https://www.irs.gov/pub/irs-pdf/fw9.pdf",
  "values": {
    "topmostSubform[0].Page1[0].f1_1[0]": "Foo Bar",
    "topmostSubform[0].Page1[0].f1_2[0]": "Baz LLC",
    "topmostSubform[0].Page1[0].FederalClassification[0].c1_1[0]": "1",
    "topmostSubform[0].Page1[0].FederalClassification[0].c1_1[1]": "Off",
    "topmostSubform[0].Page1[0].FederalClassification[0].c1_1[2]": "Off",
    "topmostSubform[0].Page1[0].FederalClassification[0].c1_1[3]": "Off",
    "topmostSubform[0].Page1[0].FederalClassification[0].c1_1[4]": "Off",
    "topmostSubform[0].Page1[0].FederalClassification[0].c1_1[5]": "Off",
    "topmostSubform[0].Page1[0].FederalClassification[0].f1_3[0]": "",
    "topmostSubform[0].Page1[0].FederalClassification[0].c1_1[6]": "Off",
    "topmostSubform[0].Page1[0].FederalClassification[0].f1_4[0]": "",
    "topmostSubform[0].Page1[0].Exemptions[0].f1_5[0]": "",
    "topmostSubform[0].Page1[0].Exemptions[0].f1_6[0]": "",
    "topmostSubform[0].Page1[0].Address[0].f1_7[0]": "123 Fake Street",
    "topmostSubform[0].Page1[0].Address[0].f1_8[0]": "Cincinnati, OH 45241",
    "topmostSubform[0].Page1[0].f1_9[0]": "",
    "topmostSubform[0].Page1[0].f1_10[0]": "",
    "topmostSubform[0].Page1[0].SSN[0].f1_11[0]": "",
    "topmostSubform[0].Page1[0].SSN[0].f1_12[0]": "",
    "topmostSubform[0].Page1[0].SSN[0].f1_13[0]": "",
    "topmostSubform[0].Page1[0].EmployerID[0].f1_14[0]": "12",
    "topmostSubform[0].Page1[0].EmployerID[0].f1_15[0]": "3456789"
  }
}'
```

The response is a [filled-in PDF](examples/filled-w9.pdf):
![Filled W-9](examples/filled-w9.png)
